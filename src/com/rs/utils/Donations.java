package com.rs.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.rs.cores.CoresManager;
import com.rs.game.player.MembershipHandler;
import com.rs.game.player.Player;

public class Donations {

	public static final void checkDonation(final Player player) {
		if (player.getTemporaryAttributtes().get("CheckingDonation") != null)
			return;
		player.getTemporaryAttributtes().put("CheckingDonation", Boolean.TRUE);
		player.getPackets().sendGameMessage("Checking donation...");
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					int productId = 0;
					double price = 0;
					URL url = new URL("https://helwyr3.com/donate/checkdonate.php?pass=pass&username="
							+ player.getUsername().toLowerCase());
					BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
					String string = reader.readLine();
					while (string != null) {
						if (string.startsWith("p")) {
							productId = Integer.parseInt(string.substring(10));
							string = reader.readLine();
						}
						if (string.startsWith("s")) {
							price = Double.parseDouble(string.substring(7));
						}
						break;
					}
					boolean noresult = string == null || string.equalsIgnoreCase("false") || productId == 0
							|| price == 0;
					reader.close();
					if (noresult) {

						player.getPackets().sendGameMessage(
								"<col=ff0000>We were unable to locate your donation, please try again later.");
					} else
						player.completeDonationProcess(player, "" + productId, "" + price, false);
				} catch (Throwable e) {
					e.printStackTrace();
					player.getPackets().sendGameMessage(
							"<col=ff0000>We were unable to verify your donation, please try again later.");
				}
				player.getTemporaryAttributtes().remove("CheckingDonation");

			}
		});
	}

	public static void donationList(Player player, String id) {
		switch (id) {
		case "38044":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getPerkManager().petmaster = true;
			player.handleDonation(20, "D'Companion");
			player.sendMessage("You've purchased: [" + Colors.red + "D'Companion</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case "38045":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getPerkManager().basher = true;
			player.handleDonation(20, "Stag�ger");
			player.sendMessage("You've purchased: [" + Colors.red + "Stag�ger</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case "38046":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getPerkManager().butcher = true;
			player.handleDonation(20, "Annihilator");
			player.sendMessage("You've purchased: [" + Colors.red + "Annihilator</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;

		case "38047":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getPerkManager().lifeSteal = true;
			player.handleDonation(20, "Dominator");
			player.sendMessage("You've purchased: [" + Colors.red + "Dominator</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case "38048":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getPerkManager().regenerator = true;
			player.handleDonation(20, "Heart of tarrasque");
			player.sendMessage("You've purchased: [" + Colors.red + "Heart of tarrasque</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case "0":
			player.sendMessage("You have not donated for anything! Donate by typing ;;store");
			break;
		case "1000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().familiarExpert = true;
			player.handleDonation(5, "Familiar Expert");
			player.sendMessage("You've purchased: [" + Colors.red + "Familiar Expert</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case "2000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().chargeBefriender = true;
			player.handleDonation(15, "Charge Befriender");
			player.sendMessage("You've purchased: [" + Colors.red + "Charge Befriender</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;

		case "3000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().charmCollector = true;
			player.handleDonation(3, "Charm Collector");
			player.sendMessage("You've purchased: [" + Colors.red + "Charm Collector</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case "4000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().coinCollector = true;
			player.handleDonation(3, "Coin Collector");
			player.sendMessage("You've purchased: [" + Colors.red + "Coin Collector</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "5000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().prayerBetrayer = true;
			player.handleDonation(10, "Prayer Betrayer");
			player.sendMessage("You've purchased: [" + Colors.red + "Prayer Betrayer</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "6000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().avasSecret = true;
			player.handleDonation(5, "Avas Secret");
			player.sendMessage("You've purchased: [" + Colors.red + "Avas Secret</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "7000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().keyExpert = true;
			player.handleDonation(6, "Key Expert");
			player.sendMessage("You've purchased: [" + Colors.red + "Key Expert</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "8000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().dragonTrainer = true;
			player.handleDonation(5, "Dragon Trainer");
			player.sendMessage("You've purchased: [" + Colors.red + "Dragon Trainer</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "9000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().gwdSpecialist = true;
			player.handleDonation(3, "GWD Specialist");
			player.sendMessage("You've purchased: [" + Colors.red + "GWD Specialist</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "10000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().dungeon = true;
			player.handleDonation(10, "Dungeons Master");
			player.sendMessage("You've purchased: [" + Colors.red + "Dungeons Master</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "11000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().petChanter = true;
			player.handleDonation(3, "Petchanter");
			player.sendMessage("You've purchased: [" + Colors.red + "Petchanter</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "12000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().perslaysion = true;
			player.handleDonation(10, "Perslaysion");
			player.sendMessage("You've purchased: [" + Colors.red + "Per'slay'sion</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "13000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().thePiromaniac = true;
			player.handleDonation(6, "Huntsman");
			player.sendMessage("You've purchased: [" + Colors.red + "The Pyromaniac</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "14000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().portsMaster = true;
			player.handleDonation(4, "Ports Master");
			player.sendMessage("You've purchased: [" + Colors.red + "Ports Master</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "15000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().greenThumb = true;
			player.handleDonation(3, "Green Thumb");
			player.sendMessage("You've purchased: [" + Colors.red + "Green Thumb</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "16000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().birdMan = true;
			player.handleDonation(1, "Bird Man");
			player.sendMessage("You've purchased: [" + Colors.red + "Bird Man</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "17000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().unbreakableForge = true;
			player.handleDonation(1, "Unbreakable Forge");
			player.sendMessage("You've purchased: [" + Colors.red + "Unbreakable Forge</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "18000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().sleightOfHand = true;
			player.handleDonation(4, "Sleight of Hand");
			player.sendMessage("You've purchased: [" + Colors.red + "Sleight of Hand</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "19000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().herbivore = true;
			player.handleDonation(8, "Herbivore");
			player.sendMessage("You've purchased: [" + Colors.red + "Herbivore</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "20000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().masterFisherman = true;
			player.handleDonation(5, "Master Fisherman");
			player.sendMessage("You've purchased: [" + Colors.red + "Master Fisherman</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "21000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().delicateCraftsman = true;
			player.handleDonation(3, "Delicate Craftsman");
			player.sendMessage("You've purchased: [" + Colors.red + "Delicate Craftsman</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "22000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().elfFiend = true;
			player.handleDonation(4, "Elf Fiend");
			player.sendMessage("You've purchased: [" + Colors.red + "Elf Fiend</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "23000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().masterChef = true;
			player.handleDonation(3, "Master Chefs Man");
			player.sendMessage("You've purchased: [" + Colors.red + "Master Chefs Man</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "24000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().masterDiviner = true;
			player.handleDonation(10, "Master Diviner");
			player.sendMessage("You've purchased: [" + Colors.red + "Master Diviner</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "25000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().quarryMaster = true;
			player.handleDonation(5, "Quarrymaster");
			player.sendMessage("You've purchased: [" + Colors.red + "Quarrymaster</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "26000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().huntsman = true;
			player.handleDonation(6, "Huntsman");
			player.sendMessage("You've purchased: [" + Colors.red + "Huntsman</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "27000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().divineDoubler = true;
			player.handleDonation(11, "Divine Doubler");
			player.sendMessage("You've purchased: [" + Colors.red + "Divine Doubler</col>]. ");

			break;

		case "28000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().imbuedFocus = true;
			player.handleDonation(5, "Imbued Focus");
			player.sendMessage("You've purchased: [" + Colors.red + "Imbued Focus</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;

		case "29000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().alchemicSmith = true;
			player.handleDonation(10, "Alchemic Smithing");
			player.sendMessage("You've purchased: [" + Colors.red + "Alchemic Smithing</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "30000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().bankCommand = true;
			player.handleDonation(25, "Bank Command");
			player.sendMessage("You've purchased: [" + Colors.red + "Bank Command</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "31000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().staminaBoost = true;
			player.handleDonation(5, "Stamina Boost");
			player.sendMessage("You've purchased: [" + Colors.red + "Stamina Boost</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "32000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().overclocked = true;
			player.handleDonation(6, "Overclocked");
			player.sendMessage("You've purchased: [" + Colors.red + "Overclocked</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "33000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().miniGamer = true;
			player.handleDonation(5, "The Mini-Gamer");
			player.sendMessage("You've purchased: [" + Colors.red + "The Mini-Gamer</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "34000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().investigator = true;
			player.handleDonation(10, "Investigator");
			player.sendMessage("You've purchased: [" + Colors.red + "Investigator</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "35000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getInventory().addItem(13663, 1);
			player.handleDonation(10, "200 Bank");
			player.sendMessage("You've purchased: [" + Colors.red + "+1 Bank Containers</col>]. "
					+ "tear the circus ticket to use.");

			break;
		case "36000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getInventory().addItem(13663, 3);
			player.handleDonation(25, "+3 Bank Containers");
			player.sendMessage("You've purchased: [" + Colors.red + "+3 Bank Containers</col>]. "
					+ "tear the circus ticket to use.");

			break;
		case "37000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getInventory().addItem(13663, 7);
			player.handleDonation(50, "+7 Bank Containers");
			player.sendMessage("You've purchased: [" + Colors.red + "+7 Bank Containers</col>]. "
					+ "tear the circus ticket to use.");

			break;
		case "38000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().petLoot = true;
			player.handleDonation(10, "petLoot");
			player.sendMessage("You've purchased: [" + Colors.red + "petLoot</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "39000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 6);
			player.getPerkManager().birdMan = true;
			player.getPerkManager().charmCollector = true;
			player.getPerkManager().coinCollector = true;
			player.getPerkManager().keyExpert = true;
			player.getPerkManager().petChanter = true;
			player.getPerkManager().petLoot = true;
			player.handleDonation(21, "Looters perk pack");
			player.sendMessage("You've purchased: [" + Colors.red + "Looters perk pack</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "40000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 15);
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
			player.handleDonation(63, "Skillers perk pack");
			player.sendMessage("You've purchased: [" + Colors.red + "Skillers perk pack</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "41000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 7);
			player.getPerkManager().bankCommand = true;
			player.getPerkManager().staminaBoost = true;
			player.getPerkManager().overclocked = true;
			player.getPerkManager().elfFiend = true;
			player.getPerkManager().miniGamer = true;
			player.getPerkManager().portsMaster = true;
			player.getPerkManager().investigator = true;
			player.handleDonation(43, "Utility perk pack");
			player.sendMessage("You've purchased: [" + Colors.red + "Utility perk pack</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "42000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 8);
			player.getPerkManager().familiarExpert = true;
			player.getPerkManager().chargeBefriender = true;
			player.getPerkManager().prayerBetrayer = true;
			player.getPerkManager().avasSecret = true;
			player.getPerkManager().dragonTrainer = true;
			player.getPerkManager().gwdSpecialist = true;
			player.getPerkManager().dungeon = true;
			player.getPerkManager().perslaysion = true;
			player.handleDonation(51, "Combatants perk pack");
			player.sendMessage("You've purchased: [" + Colors.red + "Combatants perk pack</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "515":
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
			player.getPerkManager().birdMan = true;
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
			player.getInventory().addItem(41397, 37);
			player.handleDonation(177, "Complete perk pack");
			player.sendMessage(" You've purchased: [" + Colors.red + "Complete perk pack</col>]. "
					+ "Type ;;perks to see all your game perks!");

			break;
		case "44000000":
			player.getAchManager().addKeyAmount("donator", 1);
			if (!player.getInventory().hasFreeSlots())
				player.getBank().addItem(23880, 1, true);
			else
				player.getInventory().addItem(23880, 1);
			player.handleDonation(3, "Infernal gaze aura");
			player.sendMessage("You've purchased: [" + Colors.red + "Infernal gaze aura]!</col>");
			break;

		case "45000000":
			player.getAchManager().addKeyAmount("donator", 1);
			if (!player.getInventory().hasFreeSlots())
				player.getBank().addItem(23882, 1, true);
			else
				player.getInventory().addItem(23882, 1);
			player.handleDonation(3, "Serene gaze aura");
			player.sendMessage("You've purchased: [" + Colors.red + "Serene gaze aura]!</col>");

			break;

		case "46000000":
			player.getAchManager().addKeyAmount("donator", 1);
			if (!player.getInventory().hasFreeSlots())
				player.getBank().addItem(23884, 1, true);
			else
				player.getInventory().addItem(23884, 1);
			player.handleDonation(3, "Vernal gaze aura");
			player.sendMessage("You've purchased: [" + Colors.red + "Vernal gaze aura]!</col>");

			break;

		case "47000000":
			player.getAchManager().addKeyAmount("donator", 1);
			if (!player.getInventory().hasFreeSlots())
				player.getBank().addItem(23888, 1, true);
			else
				player.getInventory().addItem(23888, 1);
			player.handleDonation(3, "Mystical gaze aura");
			player.sendMessage("You've purchased: [" + Colors.red + "Mystical gaze aura]!</col>");

			break;

		case "48000000":
			player.getAchManager().addKeyAmount("donator", 1);
			if (!player.getInventory().hasFreeSlots())
				player.getBank().addItem(23890, 1, true);
			else
				player.getInventory().addItem(23890, 1);
			player.handleDonation(3, "Blazing gaze aura");
			player.sendMessage("You've purchased: [" + Colors.red + "Blazing gaze aura]!</col>");

			break;
		case "49000000":
			player.getAchManager().addKeyAmount("donator", 1);
			if (!player.getInventory().hasFreeSlots())
				player.getBank().addItem(23886, 1, true);
			else
				player.getInventory().addItem(23886, 1);
			player.handleDonation(3, "Nocturnal gaze aura");
			player.sendMessage("You've purchased: [" + Colors.red + "Nocturnal gaze aura]!</col>");

			break;
		case "50000000":
			player.getAchManager().addKeyAmount("donator", 1);
			if (!player.getInventory().hasFreeSlots())
				player.getBank().addItem(23892, 1, true);
			else
				player.getInventory().addItem(23892, 1);
			player.handleDonation(3, "Abyssal gaze aura");
			player.sendMessage("You've purchased: [" + Colors.red + "Abyssal gaze aura]!</col>");

			break;
		case "51000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getInventory().addItem(25430, 1);
			player.handleDonation(1, "+1 Keepsake Key");
			player.sendMessage("You've purchased: [" + Colors.red + "+1 Keepsake Key</col>]. ");

			break;

		case "52000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getInventory().addItem(25430, 3);
			player.handleDonation(2, "+3 Keepsake Key");
			player.sendMessage("You've purchased: [" + Colors.red + "+3 Keepsake Key</col>]. ");

			break;

		case "53000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getInventory().addItem(25430, 7);
			player.handleDonation(3, "+7 Keepsake Key");
			player.sendMessage("You've purchased: [" + Colors.red + "+7 Keepsake Key</col>]. ");

			break;

		case "54000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getInventory().addItem(25430, 10);
			player.handleDonation(5, "+10 Keepsake Key");
			player.sendMessage("You've purchased: [" + Colors.red + "+10 Keepsake Key</col>]. ");

			break;
		case "55000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasArcaneFishing = true;
			player.handleDonation(2, "Arcane Fishing");
			player.sendMessage("You've purchased: [" + Colors.red + "Arcane Fishing</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "56000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasStrongBurial = true;
			player.handleDonation(2, "Strongarm Burial");
			player.sendMessage("You've purchased: [" + Colors.red + "Strongarm Burial</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "57000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasArcaneCook = true;
			player.handleDonation(2, "Arcane Cooking");
			player.sendMessage("You've purchased: [" + Colors.red + "Arcane Cooking</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "58000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasPowerDivination = true;
			player.handleDonation(2, "Powerful Divination");
			player.sendMessage("You've purchased: [" + Colors.red + "Powerful Divination</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "59000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasPowerConversion = true;
			player.handleDonation(2, "Powerful Conversion");
			player.sendMessage("You've purchased: [" + Colors.red + "Powerful Conversion</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;

		case "60000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasAgileDivination = true;
			player.handleDonation(2, "Agile Divination");
			player.sendMessage("You've purchased: [" + Colors.red + "Agile Divination</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "61000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasSinisterSlumber = true;
			player.handleDonation(2, "Sinister Slumber");
			player.sendMessage("You've purchased: [" + Colors.red + "Sinister Slumber</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "62000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasCrystalResting = true;
			player.handleDonation(2, "Crystal Impling Resting");
			player.sendMessage("You've purchased: [" + Colors.red + "Crystal Impling Resting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "63000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasHeadMining = true;
			player.handleDonation(2, "Headbutt Mining");
			player.sendMessage("You've purchased: [" + Colors.red + "Headbutt Mining</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "64000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasSandWalk = true;
			player.handleDonation(4, "Sandstorm Walk");
			player.sendMessage("You've purchased: [" + Colors.red + "Sandstorm Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "65000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasProudWalk = true;
			player.handleDonation(4, "Proud Walk");
			player.sendMessage("You've purchased: [" + Colors.red + "Proud Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "66000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasBarbarianWalk = true;
			player.handleDonation(4, "Barbarian Walk");
			player.sendMessage("You've purchased: [" + Colors.red + "Barbarian Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "67000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasRevenantWalk = true;
			player.handleDonation(4, "Revenant Walk");
			player.sendMessage("You've purchased: [" + Colors.red + "Revenant Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "68000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasBattleCry = true;
			player.handleDonation(2, "Slayer Battle Cry");
			player.sendMessage("You've purchased: [" + Colors.red + "Slayer Battle Cry</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "69000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasEnhancedPotion = true;
			player.handleDonation(2, "Enhanced Potion Making");
			player.sendMessage("You've purchased: [" + Colors.red + "Enhanced Potion Making</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "70000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasLumberjackWc = true;
			player.handleDonation(2, "Lumberjack Woodcutting");
			player.sendMessage("You've purchased: [" + Colors.red + "Lumberjack Woodcutting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "71000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasDeepFishing = true;
			player.handleDonation(2, "Deep-Sea Fishing");
			player.sendMessage("You've purchased: [" + Colors.red + "Deep-Sea Fishing</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "72000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasZenResting = true;
			player.handleDonation(2, "Zen Resting");
			player.sendMessage("You've purchased: [" + Colors.red + "Zen Resting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "73000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasKarateFletch = true;
			player.handleDonation(2, "Karate-Chop Fletching");
			player.sendMessage("You've purchased: [" + Colors.red + "Karate-Chop Fletching</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "74000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasIronSmith = true;
			player.handleDonation(2, "Iron-Fist Smithing");
			player.sendMessage("You've purchased: [" + Colors.red + "Iron-Fist Smithing</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "75000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasChiMining = true;
			player.handleDonation(2, "Chi-Blast Mining");
			player.sendMessage("You've purchased: [" + Colors.red + "Chi-Blast Mining</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "76000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasSamuraiCook = true;
			player.handleDonation(2, "Samurai Cooking");
			player.sendMessage("You've purchased: [" + Colors.red + "Samurai Cooking</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "77000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasRoundHouseWc = true;
			player.handleDonation(2, "Roundhouse Woodcutting");
			player.sendMessage("You've purchased: [" + Colors.red + "Roundhouse Woodcutting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;

		case "78000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasChiMining = true;
			player.handleDonation(2, "Chi-Blast Mining");
			player.sendMessage("You've purchased: [" + Colors.red + "Chi-Blast Mining</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "79000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasArcaneSmelt = true;
			player.handleDonation(2, "Arcane Smelting");
			player.sendMessage("You've purchased: [" + Colors.red + "Arcane Smelting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "80000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasArcaneResting = true;
			player.handleDonation(2, "Arcane Resting");
			player.sendMessage("You've purchased: [" + Colors.red + "Arcane Resting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "81000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasStrongWc = true;
			player.handleDonation(2, "Strongarm Woodcutting");
			player.sendMessage("You've purchased: [" + Colors.red + "Strongarm Woodcutting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "82000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasStrongMining = true;
			player.handleDonation(2, "Strongarm Mining");
			player.sendMessage("You've purchased: [" + Colors.red + "Strongarm Mining</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "83000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasSadWalk = true;
			player.handleDonation(4, "Sad Walk");
			player.sendMessage(
					"You've purchased: [" + Colors.red + "Sad Walk</col>]. " + "Talk to Solomon to toggle it on/off!");

			break;
		case "84000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasHappyWalk = true;
			player.handleDonation(4, "Happy Walk");
			player.sendMessage("You've purchased: [" + Colors.red + "Happy Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "85000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasAgileConversion = true;
			player.handleDonation(2, "Agile Conversion");
			player.sendMessage("You've purchased: [" + Colors.red + "Agile Conversion</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "86000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasStrongResting = true;
			player.handleDonation(2, "Strongarm Resting");
			player.sendMessage("You've purchased: [" + Colors.red + "Strongarm Resting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "87000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasEneResting = true;
			player.handleDonation(2, "Energy Drain Resting");
			player.sendMessage("You've purchased: [" + Colors.red + "Energy Drain Resting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");

			break;
		case "88000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getAnimations().hasArmWarrior = true;
			player.handleDonation(2, "Armchair Warrior");
			player.sendMessage("You've purchased: [" + Colors.red + "Armchair Warrior</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case "89000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getSquealOfFortune().giveBoughtSpins(5);
			player.handleDonation(2, "x5 SoF spins");
			player.sendMessage("You've purchased: [" + Colors.red + "x5 SoF spins</col>]. "
					+ "Open the Squeal of Fortune tab to use them.");
			break;
		case "90000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getSquealOfFortune().giveBoughtSpins(27);
			player.handleDonation(10, "x25 SoF spins");
			player.sendMessage("You've purchased: [" + Colors.red + "x27 SoF spins</col>]. "
					+ "Open the Squeal of Fortune tab to use them.");
			break;
		case "91000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getSquealOfFortune().giveBoughtSpins(55);
			player.handleDonation(20, "x50 SoF spins");
			player.sendMessage("You've purchased: [" + Colors.red + "x55 SoF spins</col>]. "
					+ "Open the Squeal of Fortune tab to use them.");
			break;
		case "92000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getSquealOfFortune().giveBoughtSpins(175);
			player.handleDonation(50, "x150 SoF spins");
			player.sendMessage("You've purchased: [" + Colors.red + "x175 SoF spins</col>]. "
					+ "Open the Squeal of Fortune tab to use them.");
			break;
		case "93000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getSquealOfFortune().giveBoughtSpins(350);
			player.handleDonation(100, "x300 SoF spins");
			player.sendMessage("You've purchased: [" + Colors.red + "x350 SoF spins</col>]. "
					+ "Open the Squeal of Fortune tab to use them.");
			break;
		case "94000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 5);
			player.handleDonation(5, "x5 Pot of Gold");
			player.sendMessage("You've purchased: [" + Colors.red + "5 Pot of Gold</col>]. "
					+ "Go to Members Area to Check the Shop.");
			break;
		case "95000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 10);
			player.handleDonation(10, "x10 Pot of Gold");
			player.sendMessage("You've purchased: [" + Colors.red + "10 Pot of Gold</col>]. "
					+ "Go to Members Area to Check the Shop.");
			break;

		case "96000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 22);
			player.handleDonation(20, "x20 Pot of Gold");
			player.sendMessage("You've purchased: [" + Colors.red + "20 + 2[FREE] Pot of Gold</col>]. "
					+ "Go to Members Area to Check the Shop.");
			break;
		case "97000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 55);
			player.handleDonation(50, "x50 Pot of Gold");
			player.sendMessage("You've purchased: [" + Colors.red + "50 +5[FREE] Pot of Gold</col>]. "
					+ "Go to Members Area to Check the Shop.");
			break;
		case "98000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().HazelmereLuck = true;
			player.handleDonation(15, "Hazelmere Luck");
			player.sendMessage("You've purchased: [" + Colors.red + "Hazelmere Luck</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case "99000000":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getInventory().addItem(41397, 1);
			player.getPerkManager().breath = true;
			player.handleDonation(15, "Corruption Blast");
			player.sendMessage("You've purchased: [" + Colors.red + "Corruption Blast</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;

		case "100000000":
			player.handleDonation(5, "200 cosmetic coins");
			player.sendMessage("You've purchased: [" + Colors.green
					+ "200 Helwyr coins</col> , talk to Solomon or do ;;cosmetics to access the Cosmetics Overrides].");
			player.addHelwyrCoins(200);
			break;
		case "100000001":
			player.handleDonation(10, "500 cosmetic coins");
			player.sendMessage("You've purchased: [" + Colors.green
					+ "500 Helwyr coins</col> , talk to Solomon or do ;;cosmetics to access the Cosmetics Overrides].");
			player.addHelwyrCoins(500);
			break;

		case "100000002":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getPerkManager().lifeSteal = true;
			player.handleDonation(10, "Dominator");
			player.sendMessage("You've purchased: [" + Colors.red + "Dominator</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case "100000003":
			player.getAchManager().addKeyAmount("donator", 1);
			player.getPerkManager().regenerator = true;
			player.handleDonation(10, "Heart of tarrasque");
			player.sendMessage("You've purchased: [" + Colors.red + "Heart of tarrasque</col>]. "
					+ "Type ;;perks to see all your game perks.");

			break;
		case "1231":
			player.getMembership();
			MembershipHandler.looterspack(player, true);
			player.sendMessage("You've purchased: [" + Colors.red + "Looters perk pack membership</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;

		case "1232":
			player.getMembership();
			MembershipHandler.skillerspack(player, true);
			// player.handleDonation(20, "Skillers perk pack membership");
			player.sendMessage("You've purchased: [" + Colors.red + "Skillers perk pack membership</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case "1233":
			player.getMembership();
			MembershipHandler.utilitypack(player, true);
			// player.handleDonation(14, "Utility perk pack membership");
			player.sendMessage("You've purchased: [" + Colors.red + "Utility perk pack membership</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case "1234":
			player.getMembership();
			MembershipHandler.combatpack(player, true);
			// player.handleDonation(16, "Combatant perk pack membership");
			player.sendMessage("You've purchased: [" + Colors.red + "Combatant perk pack membership</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case "1235":
			player.getMembership();
			MembershipHandler.completepack(player, true);
			player.sendMessage("You've Activated your free: [" + Colors.red + "Complete perk pack membership</col>]. "
					+ "Free 1 Week Complete Perk Package. Type ;;perks to see all your game perks.");
			break;
		/*
		 * case "1237": player.getOverrides().warlord = true; //player.handleDonation(5,
		 * "Warlord Override"); player.sendMessage("You've purchased: [" + Colors.red +
		 * "Warlord Override</col>]. " + "Talk to Solomon to toggle it on/off!"); break;
		 * case "20181000000": player.getOverrides().obsidian = true;
		 * //player.handleDonation(5, "Obsidian Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Obsidian Override</col>]. " + "Talk to Solomon to toggle it on/off!");
		 * break; case "20182000000": player.getOverrides().kalphite = true;
		 * //player.handleDonation(5, "Kalphite Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Kalphite Override</col>]. " + "Talk to Solomon to toggle it on/off!");
		 * break; case "20183000000": player.getOverrides().demonflesh = true;
		 * //player.handleDonation(5, "Demonflesh Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Demonflesh Override</col>]. " + "Talk to Solomon to toggle it on/off!");
		 * break; case "20184000000": player.getOverrides().remokee = true;
		 * //player.handleDonation(5, "Remokee Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Remokee Override</col>]. " + "Talk to Solomon to toggle it on/off!"); break;
		 * case "20185000000": player.getOverrides().assassin = true;
		 * //player.handleDonation(5, "Assassin Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Assassin Override</col>]. " + "Talk to Solomon to toggle it on/off!");
		 * break; case "20186000000": player.getOverrides().skeleton = true;
		 * //player.handleDonation(5, "Skeleton Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Skeleton Override</col>]. " + "Talk to Solomon to toggle it on/off!");
		 * break; case "20187000000": player.getOverrides().goth = true;
		 * //player.handleDonation(5, "Goth Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Goth Override</col>]. " + "Talk to Solomon to toggle it on/off!"); break;
		 * case "20188000000": player.getOverrides().mummy = true;
		 * //player.handleDonation(5, "Mummy Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Mummy Override</col>]. " + "Talk to Solomon to toggle it on/off!"); break;
		 * case "20189000000": player.getOverrides().sentinel = true;
		 * //player.handleDonation(5, "Sentinel Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Sentinel Override</col>]. " + "Talk to Solomon to toggle it on/off!");
		 * break; case "20190000000": player.getOverrides().replicaDragon = true;
		 * //player.handleDonation(5, "Replica Dragon Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Replicate Dragon Override</col>]. " +
		 * "Talk to Solomon to toggle it on/off!"); break; case "20191000000":
		 * player.getOverrides().reaver = true; //player.handleDonation(5,
		 * "Reaver Override"); player.sendMessage("You've purchased: [" + Colors.red +
		 * "Reaver Override</col>]. " + "Talk to Solomon to toggle it on/off!"); break;
		 * case "20192000000": player.getOverrides().hiker = true;
		 * //player.handleDonation(5, "Hiker Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Hiker Override</col>]. " + "Talk to Solomon to toggle it on/off!"); break;
		 * case "20193000000": player.getOverrides().skyguard = true;
		 * //player.handleDonation(5, "Skyguard Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Skyguard Override</col>]. " + "Talk to Solomon to toggle it on/off!");
		 * break; case "20194000000": player.getOverrides().vyrewatch = true;
		 * //player.handleDonation(5, "Vyrewatch Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Vyrewatch Override</col>]. " + "Talk to Solomon to toggle it on/off!");
		 * break; case "20195000000": player.getOverrides().snowman = true;
		 * //player.handleDonation(5, "Snowman Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Snowman Override</col>]. " + "Talk to Solomon to toggle it on/off!"); break;
		 * case "20196000000": player.getOverrides().samurai = true;
		 * //player.handleDonation(5, "Samurai Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Samurai Override</col>]. " + "Talk to Solomon to toggle it on/off!"); break;
		 * case "20197000000": player.getOverrides().warmWinter = true;
		 * //player.handleDonation(5, "Warm Winter Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Warm Winter Override</col>]. " + "Talk to Solomon to toggle it on/off!");
		 * break; case "20198000000": player.getOverrides().darkLord = true;
		 * //player.handleDonation(5, "Dark Lord Override");
		 * player.sendMessage("You've purchased: [" + Colors.red +
		 * "Dark Lord Override</col>]. " + "Talk to Solomon to toggle it on/off!");
		 * break;
		 */

		}
	}

	public static void HandlePromotion(Player player, int price) {
		if (price >= 20 && price < 50 && !player.isBronze()) {
			player.setDonator(true);
			int left = 59 - player.getMoneySpent();
			player.getDialogueManager().startDialogue("SimpleMessage", "You've been promoted to <img=8>Bronze member; "
					+ "Only " + left + "$ left to donate until next rank!");
			return;
		}

		if (price >= 50 && price < 100 && !player.isSilver()) {
			player.setDonator(true);
			player.setExtremeDonator(true);
			int left = 100 - player.getMoneySpent();
			player.getDialogueManager().startDialogue("SimpleMessage", "You've been promoted to <img=9>Silver member; "
					+ "Only " + left + "$ left to donate until next rank!");
			return;
		}

		if (price >= 100 && price < 250 && !player.isGold()) {
			player.setDonator(true);
			player.setExtremeDonator(true);
			player.setLegendaryDonator(true);
			int left = 250 - player.getMoneySpent();
			player.getDialogueManager().startDialogue("SimpleMessage", "You've been promoted to <img=12>Gold member; "
					+ "Only " + left + "$ left to donate until next rank!");
			return;
		}

		if (price >= 250 && price < 500 && !player.isPlatinum()) {
			player.setDonator(true);
			player.setExtremeDonator(true);
			player.setLegendaryDonator(true);
			player.setSupremeDonator(true);
			int left = 500 - player.getMoneySpent();
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You've been promoted to <img=13>Platinum member; " + "Only " + left
							+ "$ left to donate until next rank!");
			return;
		}

		if (price >= 500 && !player.isDiamond()) {
			player.setDonator(true);
			player.setExtremeDonator(true);
			player.setLegendaryDonator(true);
			player.setSupremeDonator(true);
			player.setUltimateDonator(true);
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You've been promoted to <img=14>Diamond member; " + "This is the highest rank available!");
			return;
		}

		if (player.getMoneySpent() < 20 && !player.isBronze()) {
			int left = 20 - player.getMoneySpent();
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Only " + left + "$ left " + "to donate until promotion to Bronze member!");
			return;
		}
		if (player.getMoneySpent() >= 20 && !player.isBronze()) {
			player.setDonator(true);
			int left = 50 - player.getMoneySpent();
			player.getDialogueManager().startDialogue("SimpleMessage", "You've been promoted to <img=8>Bronze member; "
					+ "Only " + left + "$ left to donate until next rank!");
			return;
		}
		if (player.getMoneySpent() >= 50 && !player.isSilver()) {
			player.setExtremeDonator(true);
			int left = 100 - player.getMoneySpent();
			player.getDialogueManager().startDialogue("SimpleMessage", "You've been promoted to <img=9>Silver member; "
					+ "Only " + left + "$ left to donate until next rank!");
			return;
		}
		if (player.getMoneySpent() >= 100 && !player.isGold()) {
			player.setLegendaryDonator(true);
			int left = 250 - player.getMoneySpent();
			player.getDialogueManager().startDialogue("SimpleMessage", "You've been promoted to <img=12>Gold member; "
					+ "Only " + left + "$ left to donate until next rank!");
			return;
		}
		if (player.getMoneySpent() >= 250 && !player.isPlatinum()) {
			player.setSupremeDonator(true);
			int left = 500 - player.getMoneySpent();
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You've been promoted to <img=13>Platinum member; " + "Only " + left
							+ "$ left to donate until next rank!");
			return;
		}
		if (player.getMoneySpent() >= 500 && !player.isDiamond()) {
			player.setUltimateDonator(true);
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You've been promoted to <img=14>Diamond member; " + "This is the highest rank available!");
			return;
		}

	}

}