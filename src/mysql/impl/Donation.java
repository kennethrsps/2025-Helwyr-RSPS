package mysql.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.utils.Colors;

/**
 * Using this class: To call this class, it's best to make a new thread. You can
 * do it below like so: new Thread(new Donation(player)).start();
 */
public class Donation implements Runnable {

	public static final String HOST = "166.62.28.100";
	public static final String USER = "helwyrstore";
	public static final String PASS = "pogiko";
	public static final String DATABASE = "helwyrstore";

	private Player player;
	private Connection conn;
	private Statement stmt;

	/**
	 * The constructor
	 * 
	 * @param player
	 */
	public Donation(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		try {
			if (!connect(HOST, DATABASE, USER, PASS)) {
				return;
			}

			String name = player.getUsername().replace("_", " ");
			ResultSet rs = executeQuery(
					"SELECT * FROM payments WHERE player_name='" + name + "' AND status='Completed' AND claimed=0");

			while (rs.next()) {
				int item_number = rs.getInt("item_number");
				double paid = rs.getDouble("amount");
				int quantity = rs.getInt("quantity");

				switch (item_number) {// add products according to their ID in the ACP

			/*	case 22: // example
					player.getAchManager().addKeyAmount("donator", 1);
					player.handleDonation(1 * quantity, "Store Credit");
					player.getInventory().addItem(34896, 1 * quantity);
					player.sendMessage("You've purchased: [" + Colors.red + "1usd Store credit.</col>] "
							+ "Open the Store credit to claim the credit!.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated </col>" + Colors.red + "x" + quantity
							+ "</col> 1 usd Store Credit! and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;
				case 23: // example
					player.getAchManager().addKeyAmount("donator", 1);
					player.handleDonation(5 * quantity, "Store Credit");
					player.getInventory().addItem(34896, 5 * quantity);
					player.sendMessage("You've purchased: [" + Colors.red + "5 usd Store credit.</col>] "
							+ "Open the Store credit to claim the credit!.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated </col>" + Colors.red + "x" + quantity
							+ "</col> 5 usd Store Credit! and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;*/
				case 55: // example
					player.getAchManager().addKeyAmount("donator", 1);
					player.handleDonation(10 * quantity, "Store Credit");
					player.getInventory().addItem(34896, 10 * quantity);
					player.sendMessage("You've purchased: [" + Colors.red + "10 usd Store credit.</col>] "
							+ "Open the Store credit to claim the credit!.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated </col>" + Colors.red + "x" + quantity
							+ "</col> 10 usd Store Credit! and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;
				/*case 25: // example
					player.getAchManager().addKeyAmount("donator", 1);
					player.handleDonation(20 * quantity, "Store Credit");
					player.getInventory().addItem(34896, 20 * quantity);
					player.sendMessage("You've purchased: [" + Colors.red + "20 usd Store credit.</col>] "
							+ "Open the Store credit to claim the credit!.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated </col>" + Colors.red + "x" + quantity
							+ "</col> 20 usd Store Credit! and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;

				case 26: // example
					player.getAchManager().addKeyAmount("donator", 1);
					player.handleDonation(50 * quantity, "Store Credit");
					player.getInventory().addItem(34896, 50 * quantity);
					player.sendMessage("You've purchased: [" + Colors.red + "50 usd Store credit.</col>] "
							+ "Open the Store credit to claim the credit!.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated </col>" + Colors.red + "x" + quantity
							+ "</col> 50 usd Store Credit! and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;*/

				/*case 27:
					player.getAchManager().addKeyAmount("donator", 1);
					player.handleDonation(100 * quantity, "Store Credit");
					player.getInventory().addItem(34896, 100 * quantity);
					player.sendMessage("You've purchased: [" + Colors.red + "100 usd Store credit.</col>] "
							+ "Open the Store credit to claim the credit!.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated </col>" + Colors.red + "x" + quantity
							+ "</col> 100 usd Store Credit! and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;*/

				case 30:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().familiarExpert = true;
					player.handleDonation(5 * quantity, "Familiar Expert");
					player.sendMessage("You've purchased: [" + Colors.red + "Familiar Expert</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Familiar Expert"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 51:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().chargeBefriender = true;
					player.handleDonation(15 * quantity, "Charge Befriender");
					player.sendMessage("You've purchased: [" + Colors.red + "Charge Befriender</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Charge Befriender"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 20:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().charmCollector = true;
					player.handleDonation(3 * quantity, "Charm Collector");
					player.sendMessage("You've purchased: [" + Colors.red + "Charm Collector</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Charm Collector"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 21:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().coinCollector = true;
					player.handleDonation(3 * quantity, "Coin Collector");
					player.sendMessage("You've purchased: [" + Colors.red + "Coin Collector</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Coin Collector"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 45:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().prayerBetrayer = true;
					player.handleDonation(10 * quantity, "Prayer Betrayer");
					player.sendMessage("You've purchased: [" + Colors.red + "Prayer Betrayer</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Prayer Betrayer"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 31:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().avasSecret = true;
					player.handleDonation(5 * quantity, "Avas Secret");
					player.sendMessage("You've purchased: [" + Colors.red + "Avas Secret</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Avas Secret"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 39:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().keyExpert = true;
					player.handleDonation(6 * quantity, "Key Expert");
					player.sendMessage("You've purchased: [" + Colors.red + "Key Expert</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Key Expert" + "</col>] and has now a total of "
							+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
					break;

				case 32:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().dragonTrainer = true;
					player.handleDonation(5 * quantity, "Dragon Trainer");
					player.sendMessage("You've purchased: [" + Colors.red + "Dragon Trainer</col>]. "
							+ "Type ;;perks to see all your game perks.");

					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Dragon Trainer"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 22:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().gwdSpecialist = true;
					player.handleDonation(3 * quantity, "GWD Specialist");
					player.sendMessage("You've purchased: [" + Colors.red + "GWD Specialist</col>]. "
							+ "Type ;;perks to see all your game perks.");

					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "GWD Specialist"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 46:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().dungeon = true;
					player.handleDonation(10 * quantity, "Dungeons Master");
					player.sendMessage("You've purchased: [" + Colors.red + "Dungeons Master</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Dungeons Master"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 23:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().petChanter = true;
					player.handleDonation(3 * quantity, "Petchanter");
					player.sendMessage("You've purchased: [" + Colors.red + "Petchanter</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Petchanter" + "</col>] and has now a total of "
							+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
					break;

				case 47:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().perslaysion = true;
					player.handleDonation(10 * quantity, "Perslaysion");
					player.sendMessage("You've purchased: [" + Colors.red + "Per'slay'sion</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Per'slay'sion"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 27:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().portsMaster = true;
					player.handleDonation(4 * quantity, "Ports Master");
					player.sendMessage("You've purchased: [" + Colors.red + "Ports Master</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Ports Master"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 33:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().thePiromaniac = true;
					player.handleDonation(5 * quantity, "thePiromaniac");
					player.sendMessage("You've purchased: [" + Colors.red + "The Pyromaniac</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "The Pyromaniac"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 24:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().greenThumb = true;
					player.handleDonation(3 * quantity, "Green Thumb");
					player.sendMessage("You've purchased: [" + Colors.red + "Green Thumb</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Green Thumb"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 18:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().birdMan = true;
					player.handleDonation(1 * quantity, "Bird Man");
					player.sendMessage("You've purchased: [" + Colors.red + "Bird Man</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Bird Man" + "</col>] and has now a total of "
							+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
					break;

				case 28:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().sleightOfHand = true;
					player.handleDonation(4 * quantity, "Sleight of Hand");
					player.sendMessage("You've purchased: [" + Colors.red + "Sleight of Hand</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Sleight of Hand"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 4544:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().herbivore = true;
					player.handleDonation(8 * quantity, "Herbivore");
					player.sendMessage("You've purchased: [" + Colors.red + "Herbivore</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Herbivore" + "</col>] and has now a total of "
							+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
					break;

				case 34:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().masterFisherman = true;
					player.handleDonation(5 * quantity, "Master Fisherman");
					player.sendMessage("You've purchased: [" + Colors.red + "Master Fisherman</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Master Fisherman"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 25:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().delicateCraftsman = true;
					player.handleDonation(3 * quantity, "Delicate Craftsman");
					player.sendMessage("You've purchased: [" + Colors.red + "Delicate Craftsman</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Delicate Craftsman"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 29:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().elfFiend = true;
					player.handleDonation(4 * quantity, "Elf Fiend");
					player.sendMessage("You've purchased: [" + Colors.red + "Elf Fiend</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Elf Fiend" + "</col>] and has now a total of "
							+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
					break;

				case 26:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().masterChef = true;
					player.handleDonation(3 * quantity, "Master Chefs Man");
					player.sendMessage("You've purchased: [" + Colors.red + "Master Chefs Man</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Elf Fiend" + "</col>] and has now a total of "
							+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
					break;

				case 48:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().masterDiviner = true;
					player.handleDonation(10 * quantity, "Master Diviner");
					player.sendMessage("You've purchased: [" + Colors.red + "Master Diviner</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Master Diviner"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 35:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().quarryMaster = true;
					player.handleDonation(5 * quantity, "Quarrymaster");
					player.sendMessage("You've purchased: [" + Colors.red + "Quarrymaster</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Quarrymaster"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 40:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().huntsman = true;
					player.handleDonation(6 * quantity, "Huntsman");
					player.sendMessage("You've purchased: [" + Colors.red + "Huntsman</col>]. "
							+ "Type ;;perks to see all your game perks.");
					;
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Huntsman" + "</col>] and has now a total of "
							+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
					break;

				case 41:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().masterFledger = true;
					player.handleDonation(6 * quantity, "masterFledger");
					player.sendMessage("You've purchased: [" + Colors.red + "Master Fledger</col>]. "
							+ "Type ;;perks to see all your game perks.");
					;
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Master Fledger"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 49:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().divineDoubler = true;
					player.handleDonation(10 * quantity, "Divine Doubler");
					player.sendMessage("You've purchased: [" + Colors.red + "Divine Doubler</col>]. ");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Divine Doubler"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 36:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().imbuedFocus = true;
					player.handleDonation(5 * quantity, "Imbued Focus");
					player.sendMessage("You've purchased: [" + Colors.red + "Imbued Focus</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Imbued Focus"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 43:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().alchemicSmith = true;
					player.handleDonation(7 * quantity, "Alchemic Smithing");
					player.sendMessage("You've purchased: [" + Colors.red + "Alchemic Smithing</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Alchemic Smithing"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 52:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().bankCommand = true;
					player.handleDonation(25 * quantity, "Bank Command");
					player.sendMessage("You've purchased: [" + Colors.red + "Bank Command</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Bank Command"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 37:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().staminaBoost = true;
					player.handleDonation(10 * quantity, "Stamina Boost");
					player.sendMessage("You've purchased: [" + Colors.red + "Stamina Boost</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Stamina Boost"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 42:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().overclocked = true;
					player.handleDonation(6 * quantity, "Overclocked");
					player.sendMessage("You've purchased: [" + Colors.red + "Overclocked</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Stamina Boost"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 38:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().miniGamer = true;
					player.handleDonation(5 * quantity, "The Mini-Gamer");
					player.sendMessage("You've purchased: [" + Colors.red + "The Mini-Gamer</col>]. "
							+ "Type ;;perks to see all your game perks.");

					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "The Mini-Gamer"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 50:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().investigator = true;
					player.handleDonation(15 * quantity, "Investigator");
					player.sendMessage("You've purchased: [" + Colors.red + "Investigator</col>]. "
							+ "Type ;;perks to see all your game perks.");

					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Investigator"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 63:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().greenThumb = true;
					player.getPerkManager().unbreakableForge = true;
					player.getPerkManager().sleightOfHand = true;
					player.getPerkManager().herbivore = true;
					player.getPerkManager().masterFisherman = true;
					player.getPerkManager().delicateCraftsman = true;
					player.getPerkManager().masterChef = true;
					player.getPerkManager().masterDiviner = true;
					player.getPerkManager().quarryMaster = true;
					player.getPerkManager().masterFledger = true;
					player.getPerkManager().thePiromaniac = true;
					player.getPerkManager().huntsman = true;
					player.getPerkManager().divineDoubler = true;
					player.getPerkManager().imbuedFocus = true;
					player.getPerkManager().alchemicSmith = true;
					player.handleDonation(75 * quantity, "Skillers perk pack");
					player.sendMessage("You've purchased: [" + Colors.red + "Skillers perk pack</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Skillers perk pack"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 64:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().bankCommand = true;
					player.getPerkManager().staminaBoost = true;
					player.getPerkManager().overclocked = true;
					player.getPerkManager().elfFiend = true;
					player.getPerkManager().miniGamer = true;
					player.getPerkManager().portsMaster = true;
					player.getPerkManager().investigator = true;
					player.handleDonation(50 * quantity, "Utility perk pack");
					player.sendMessage("You've purchased: [" + Colors.red + "Utility perk pack</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Utility perk pack"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 65:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().familiarExpert = true;
					player.getPerkManager().chargeBefriender = true;
					player.getPerkManager().prayerBetrayer = true;
					player.getPerkManager().avasSecret = true;
					player.getPerkManager().dragonTrainer = true;
					player.getPerkManager().gwdSpecialist = true;
					player.getPerkManager().dungeon = true;
					player.getPerkManager().perslaysion = true;
					player.handleDonation(87 * quantity, "Combatants perk pack");
					player.sendMessage("You've purchased: [" + Colors.red + "Combatants perk pack</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Combatants perk pack"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 66:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().birdMan = true;
					player.getPerkManager().charmCollector = true;
					player.getPerkManager().coinCollector = true;
					player.getPerkManager().keyExpert = true;
					player.getPerkManager().petChanter = true;
					player.getPerkManager().petLoot = true;
					player.getPerkManager().greenThumb = true;
					player.getPerkManager().unbreakableForge = true;
					player.getPerkManager().sleightOfHand = true;
					player.getPerkManager().herbivore = true;
					player.getPerkManager().masterFisherman = true;
					player.getPerkManager().delicateCraftsman = true;
					player.getPerkManager().masterChef = true;
					player.getPerkManager().masterDiviner = true;
					player.getPerkManager().quarryMaster = true;
					player.getPerkManager().masterFledger = true;
					player.getPerkManager().thePiromaniac = true;
					player.getPerkManager().huntsman = true;
					player.getPerkManager().divineDoubler = true;
					player.getPerkManager().imbuedFocus = true;
					player.getPerkManager().alchemicSmith = true;
					player.getPerkManager().bankCommand = true;
					player.getPerkManager().staminaBoost = true;
					player.getPerkManager().overclocked = true;
					player.getPerkManager().elfFiend = true;
					player.getPerkManager().miniGamer = true;
					player.getPerkManager().portsMaster = true;
					player.getPerkManager().investigator = true;
					player.getPerkManager().familiarExpert = true;
					player.getPerkManager().chargeBefriender = true;
					player.getPerkManager().prayerBetrayer = true;
					player.getPerkManager().avasSecret = true;
					player.getPerkManager().dragonTrainer = true;
					player.getPerkManager().gwdSpecialist = true;
					player.getPerkManager().dungeon = true;
					player.getPerkManager().perslaysion = true;
					player.handleDonation(213 * quantity, "Complete perk pack");
					player.sendMessage(" You've purchased: [" + Colors.red + "Complete perk pack</col>]. "
							+ "Type ;;perks to see all your game perks!");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Complete perk pack"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 660:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getSquealOfFortune().giveBoughtSpins(5 * quantity);
					player.handleDonation(2 * quantity, "x5 SoF spins");
					player.sendMessage("You've purchased: [" + Colors.red + "x5 SoF spins</col>]. "
							+ "Open the Squeal of Fortune tab to use them.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for </col>" + Colors.red + "x" + quantity + "</col> [" + Colors.yellow
							+ "x5 SoF spins" + "</col>] and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;

				case 56:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getSquealOfFortune().giveBoughtSpins(27 * quantity);
					player.handleDonation(10 * quantity, "x25 SoF spins");
					player.sendMessage("You've purchased: [" + Colors.red + "x27 SoF spins</col>]. "
							+ "Open the Squeal of Fortune tab to use them.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for </col>" + Colors.red + "x" + quantity + "</col> [" + Colors.yellow
							+ "x27 SoF spins" + "</col>] and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;

				case 57:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getSquealOfFortune().giveBoughtSpins(55 * quantity);
					player.handleDonation(20 * quantity, "x50 SoF spins");
					player.sendMessage("You've purchased: [" + Colors.red + "x55 SoF spins</col>]. "
							+ "Open the Squeal of Fortune tab to use them.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for </col>" + Colors.red + "x" + quantity + "</col> [" + Colors.yellow
							+ "x55 SoF spins" + "</col>] and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;

				case 69:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getSquealOfFortune().giveBoughtSpins(175 * quantity);
					player.handleDonation(50 * quantity, "x150 SoF spins");
					player.sendMessage("You've purchased: [" + Colors.red + "x175 SoF spins</col>]. "
							+ "Open the Squeal of Fortune tab to use them.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for </col>" + Colors.red + "x" + quantity + "</col> [" + Colors.yellow
							+ "x175 SoF spins" + "</col>] and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;

				case 53:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getInventory().addItem(41397, 5 * quantity);
					player.handleDonation(5 * quantity, "x5 Pot of Gold");
					player.sendMessage("You've purchased: [" + Colors.red + "5 Pot of Gold</col>]. "
							+ "Go to Members Area to Check the Shop.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for </col>" + Colors.red + "x" + quantity + "</col> [" + Colors.yellow
							+ "5 Pot of Gold" + "</col>] and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;

				case 71:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getInventory().addItem(41397, 10 * quantity);
					player.handleDonation(10 * quantity, "x10 Pot of Gold");
					player.sendMessage("You've purchased: [" + Colors.red + "10 Pot of Gold</col>]. "
							+ "Go to Members Area to Check the Shop.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for </col>" + Colors.red + "x" + quantity + "</col> [" + Colors.yellow
							+ "10 Pot of Gold" + "</col>] and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;

				case 54:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getInventory().addItem(41397, 22 * quantity);
					player.handleDonation(20 * quantity, "x20 Pot of Gold");
					player.sendMessage("You've purchased: [" + Colors.red + "20 + 2[FREE] Pot of Gold</col>]. "
							+ "Go to Members Area to Check the Shop.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for </col>" + Colors.red + "x" + quantity + "</col> [" + Colors.yellow
							+ "20 + 2[FREE] Pot of Gold" + "</col>] and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;

				case 73:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getInventory().addItem(41397, 55 * quantity);
					player.handleDonation(50 * quantity, "x50 Pot of Gold");
					player.sendMessage("You've purchased: [" + Colors.red + "50 +5[FREE] Pot of Gold</col>]. "
							+ "Go to Members Area to Check the Shop.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for</col>" + Colors.red + "x" + quantity + "</col> [" + Colors.yellow
							+ "50 +5[FREE] Pot of Gold" + "</col>] and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;

				case 74:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getInventory().addItem(41397, 110 * quantity);
					player.handleDonation(100 * quantity, "x100 Pot of Gold");
					player.sendMessage("You've purchased: [" + Colors.red + "100 +10[FREE] Pot of Gold </col>]. "
							+ "Go to Members Area to Check the Shop.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for</col>" + Colors.red + "x" + quantity + "</col> [" + Colors.yellow
							+ "100 +10[FREE] Pot of Gold" + "</col>] and has now a total of " + Colors.red + "$"
							+ player.getMoneySpent() + " Donation.", false);
					break;

				case 60:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().petmaster = true;
					player.handleDonation(20 * quantity, "D'Companion");
					player.sendMessage("You've purchased: [" + Colors.red + "D'Companion</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "D'Companion"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 61:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().basher = true;
					player.handleDonation(20 * quantity, "Stag·ger");
					player.sendMessage("You've purchased: [" + Colors.red + "Stag·ger</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Stag·ger" + "</col>] and has now a total of "
							+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
					break;

				case 62:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().butcher = true;
					player.handleDonation(20 * quantity, "Annihilator");
					player.sendMessage("You've purchased: [" + Colors.red + "Annihilator</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Annihilator"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 58:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().lifeSteal = true;
					player.handleDonation(10 * quantity, "Dominator");
					player.sendMessage("You've purchased: [" + Colors.red + "Dominator</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Dominator" + "</col>] and has now a total of "
							+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
					break;

				case 59:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getPerkManager().regenerator = true;
					player.handleDonation(10 * quantity, "Heart of tarrasque");
					player.sendMessage("You've purchased: [" + Colors.red + "Heart of tarrasque</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Heart of tarrasque"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;

				case 19:
					player.getAchManager().addKeyAmount("donator", 1);
					player.getInventory().addItem(41397, 1);
					player.getPerkManager().unbreakableForge = true;
					player.handleDonation(1 * quantity, "Unbreakable Forge");
					player.sendMessage("You've purchased: [" + Colors.red + "Unbreakable Forge</col>]. "
							+ "Type ;;perks to see all your game perks.");
					World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
							+ " </col> Donated for [" + Colors.yellow + "Unbreakable Forge"
							+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
							+ " Donation.", false);
					break;
				}

				rs.updateInt("claimed", 1); // do not delete otherwise they can reclaim!
				rs.updateRow();
			}

			destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param host
	 *            the host ip address or url
	 * @param database
	 *            the name of the database
	 * @param user
	 *            the user attached to the database
	 * @param pass
	 *            the users password
	 * @return true if connected
	 */
	public boolean connect(String host, String database, String user, String pass) {
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + database, user, pass);
			return true;
		} catch (SQLException e) {
			System.out.println("Failing connecting to database!");
			return false;
		}
	}

	/**
	 * Disconnects from the MySQL server and destroy the connection and statement
	 * instances
	 */
	public void destroy() {
		try {
			conn.close();
			conn = null;
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Executes an update query on the database
	 * 
	 * @param query
	 * @see {@link Statement#executeUpdate}
	 */
	public int executeUpdate(String query) {
		try {
			this.stmt = this.conn.createStatement(1005, 1008);
			int results = stmt.executeUpdate(query);
			return results;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	/**
	 * Executres a query on the database
	 * 
	 * @param query
	 * @see {@link Statement#executeQuery(String)}
	 * @return the results, never null
	 */
	public ResultSet executeQuery(String query) {
		try {
			this.stmt = this.conn.createStatement(1005, 1008);
			ResultSet results = stmt.executeQuery(query);
			return results;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
