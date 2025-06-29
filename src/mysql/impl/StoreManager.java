package mysql.impl;
import java.sql.ResultSet;

import mysql.Database;
import com.rs.game.player.Player;
import com.rs.utils.Colors;
import com.rs.game.item.Item;

/**
 * Handles donations
* @author Zeus
 *
 */

public class StoreManager implements Runnable {

	public static final String HOST_ADDRESS = "143.95.234.5";
	public static final String USERNAME = "neardeso_donate";
	public static final String PASSWORD = "DavidRandom111";
	public static final String DATABASE = "neardeso_donate";
	
	private Player player;
	
	@Override
	public void run() {
		try {
			Database db = new Database(HOST_ADDRESS, USERNAME, PASSWORD, DATABASE);
			
			if (!db.init()) {
				System.err.println("[Donation] Failed to connect to database!");
				return;
			}
			
			String name = player.getUsername().replace("_", " ");
			ResultSet rs = db.executeQuery("SELECT * FROM payments WHERE player_name='"+name+"' AND claimed=0");
			
			while(rs.next()) {
				String item_name = rs.getString("item_name");
				int item_number = rs.getInt("item_number");
				double amount = rs.getDouble("amount");
				int quantity = rs.getInt("quantity");
				
				ResultSet result = db.executeQuery("SELECT * FROM products WHERE item_id="+item_number+" LIMIT 1");
				
				if (result == null || !result.next()
						|| !result.getString("item_name").equalsIgnoreCase(item_name)
						|| result.getDouble("item_price") != amount
						|| quantity < 1 || quantity > Integer.MAX_VALUE) {
					System.out.println("[Donation] Invalid purchase for "+name+" (item: "+item_name+", id: "+item_number+")");
					continue;
				}
				
				handleItems(item_number);
				rs.updateInt("claimed", 1);
				rs.updateRow();
			}
			
			db.destroyAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleItems(int productId) {
		switch(productId) {
		case 0:
			player.sendMessage("You have not donated for anything! Donate by typing ;;store");
		case 51:
			
			break;
		case 68:
			
			break;
		case 115:
			
			break;
		case 111:
			
			break;
		case 125:
			
			break;
		case 124:
			
			break;
		case 58:
			
			break;
		case 53:
			
			break;
		case 54:
			;
			break;
		case 116:
			
			break;
		case 121:
			
			break;
		case 112:
			
			break;
		case 55:
			
			break;
		case 64:
			
			break;
		case 52:
			
			break;
		case 60:
			
			break;
		case 56:
			
			break;
		case 59:
			
			break;
		case 57:
			
			break;
		case 62:
			
			break;
		case 61:
			
			break;
		case 67:
			
			break;
		case 114:
			
			break;
		case 119:
			
			break;
		case 71:
			
			break;
		case 72:
			
			break;
		case 70:
			
			break;
		case 73:
			
			break;
		case 74:
			
			break;
		case 120:
			
			break;
		case 123:
			
			break;
		case 69:
			
			break;
		case 122:
			player.getPerkManager().masterFledger = true;
			player.handleDonation(6, "Master Fledger");
			player.sendMessage("You've purchased: ["+Colors.red+"Master Fledger</col>]. "
					+ "Type ;;perks to see all your game perks.");
			break;
		case 75:
			
			break;
		case 76:
			
			break;
		case 77:
			;
			break;
		case 78:
			
			break;
		case 79:
			
			break;
		case 117:
			
			break;
		case 65:
			
			break;
		case 97:
			
			break;
		case 108:
			
			break;
		case 96:
			
			break;
		case 92:
			
			break;
		case 87:
			
			break;
		case 98:
			
			break;
		case 84:
			
			break;
		case 103:
			
			break;
		case 89:
			
			break;
		case 93:
			
			break;
		case 99:
			
			break;
		case 101:
			
			break;
		case 80:
			
		case 90:
			
			break;
		case 104:
			
			break;
		case 107:
			
			break;
		case 105:
			
			break;
		case 100:
			
			break;
		case 109:
			
			break;
		case 86:
			
			break;
		case 45:
			player.getAnimations().hasBattleCry = true;
			player.handleDonation(2, "Slayer Battle Cry");
			player.sendMessage("You've purchased: ["+Colors.red+"Slayer Battle Cry</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 30:
			player.getAnimations().hasEnhancedPotion = true;
			player.handleDonation(2, "Enhanced Potion Making");
			player.sendMessage("You've purchased: ["+Colors.red+"Enhanced Potion Making</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 35:
			player.getAnimations().hasLumberjackWc = true;
			player.handleDonation(2, "Lumberjack Woodcutting");
			player.sendMessage("You've purchased: ["+Colors.red+"Lumberjack Woodcutting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 28:
			player.getAnimations().hasDeepFishing = true;
			player.handleDonation(2, "Deep-Sea Fishing");
			player.sendMessage("You've purchased: ["+Colors.red+"Deep-Sea Fishing</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 50:
			player.getAnimations().hasZenResting = true;
			player.handleDonation(2, "Zen Resting");
			player.sendMessage("You've purchased: ["+Colors.red+"Zen Resting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 34:
			player.getAnimations().hasKarateFletch = true;
			player.handleDonation(2, "Karate-Chop Fletching");
			player.sendMessage("You've purchased: ["+Colors.red+"Karate-Chop Fletching</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 33:
			player.getAnimations().hasIronSmith = true;
			player.handleDonation(2, "Iron-Fist Smithing");
			player.sendMessage("You've purchased: ["+Colors.red+"Iron-Fist Smithing</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 26:
			player.getAnimations().hasChiMining = true;
			player.handleDonation(2, "Chi-Blast Mining");
			player.sendMessage("You've purchased: ["+Colors.red+"Chi-Blast Mining</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 42:
			player.getAnimations().hasSamuraiCook = true;
			player.handleDonation(2, "Samurai Cooking");
			player.sendMessage("You've purchased: ["+Colors.red+"Samurai Cooking</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 40:
			player.getAnimations().hasRoundHouseWc = true;
			player.handleDonation(2, "Roundhouse Woodcutting");
			player.sendMessage("You've purchased: ["+Colors.red+"Roundhouse Woodcutting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 25:
			player.getAnimations().hasBlastMining = true;
			player.handleDonation(2, "Blast Mining");
			player.sendMessage("You've purchased: ["+Colors.red+"Blast Mining</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 48:
			player.getAnimations().hasStrongResting = true;
			player.handleDonation(2, "Strongarm Resting");
			player.sendMessage("You've purchased: ["+Colors.red+"Strongarm Resting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 110:
			player.getAnimations().hasArcaneSmelt = true;
			player.handleDonation(2, "Arcane Smelting");
			player.sendMessage("You've purchased: ["+Colors.red+"Arcane Smelting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 126:
			player.getAnimations().hasArcaneResting = true;
			player.handleDonation(2, "Arcane Resting");
			player.sendMessage("You've purchased: ["+Colors.red+"Arcane Resting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 49:
			player.getAnimations().hasStrongWc = true;
			player.handleDonation(2, "Strongarm Woodcutting");
			player.sendMessage("You've purchased: ["+Colors.red+"Strongarm Woodcutting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 47:
			player.getAnimations().hasStrongMining = true;
			player.handleDonation(2, "Strongarm Mining");
			player.sendMessage("You've purchased: ["+Colors.red+"Strongarm Mining</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 23:
			player.getAnimations().hasArcaneFishing = true;
			player.handleDonation(2, "Arcane Fishing");
			player.sendMessage("You've purchased: ["+Colors.red+"Arcane Fishing</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 46:
			player.getAnimations().hasStrongBurial = true;
			player.handleDonation(2, "Strongarm Burial");
			player.sendMessage("You've purchased: ["+Colors.red+"Strongarm Burial</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 127:
			player.getAnimations().hasArcaneCook = true;
			player.handleDonation(2, "Arcane Cooking");
			player.sendMessage("You've purchased: ["+Colors.red+"Arcane Cooking</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 37:
			player.getAnimations().hasPowerDivination = true;
			player.handleDonation(2, "Powerful Divination");
			player.sendMessage("You've purchased: ["+Colors.red+"Powerful Divination</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 36:
			player.getAnimations().hasPowerConversion = true;
			player.handleDonation(2, "Powerful Conversion");
			player.sendMessage("You've purchased: ["+Colors.red+"Powerful Conversion</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 128:
			player.getAnimations().hasAgileDivination = true;
			player.handleDonation(2, "Agile Divination");
			player.sendMessage("You've purchased: ["+Colors.red+"Agile Divination</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 44:
			player.getAnimations().hasSinisterSlumber = true;
			player.handleDonation(2, "Sinister Slumber");
			player.sendMessage("You've purchased: ["+Colors.red+"Sinister Slumber</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 22:
			player.getAnimations().hasArmWarrior = true;
			player.handleDonation(2, "Armchair Warrior");
			player.sendMessage("You've purchased: ["+Colors.red+"Armchair Warrior</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 29:
			player.getAnimations().hasEneResting = true;
			player.handleDonation(2, "Energy Drain Resting");
			player.sendMessage("You've purchased: ["+Colors.red+"Energy Drain Resting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 27:
			player.getAnimations().hasCrystalResting = true;
			player.handleDonation(2, "Crystal Impling Resting");
			player.sendMessage("You've purchased: ["+Colors.red+"Crystal Impling Resting</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 32:
			player.getAnimations().hasHeadMining = true;
			player.handleDonation(2, "Headbutt Mining");
			player.sendMessage("You've purchased: ["+Colors.red+"Headbutt Mining</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 43:
			player.getAnimations().hasSandWalk = true;
			player.handleDonation(4, "Sandstorm Walk");
			player.sendMessage("You've purchased: ["+Colors.red+"Sandstorm Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 41:
			player.getAnimations().hasSadWalk = true;
			player.handleDonation(4, "Sad Walk");
			player.sendMessage("You've purchased: ["+Colors.red+"Sad Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 91930126:
			player.getAnimations().hasAngryWalk = true;
			player.handleDonation(4, "Angry Walk");
			player.sendMessage("You've purchased: ["+Colors.red+"Angry Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 38:
			player.getAnimations().hasProudWalk = true;
			player.handleDonation(4, "Proud Walk");
			player.sendMessage("You've purchased: ["+Colors.red+"Proud Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 31:
			player.getAnimations().hasHappyWalk = true;
			player.handleDonation(4, "Happy Walk");
			player.sendMessage("You've purchased: ["+Colors.red+"Happy Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 24:
			player.getAnimations().hasBarbarianWalk = true;
			player.handleDonation(4, "Barbarian Walk");
			player.sendMessage("You've purchased: ["+Colors.red+"Barbarian Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 39:
			player.getAnimations().hasRevenantWalk = true;
			player.handleDonation(4, "Revenant Walk");
			player.sendMessage("You've purchased: ["+Colors.red+"Revenant Walk</col>]. "
					+ "Talk to Solomon to toggle it on/off!");
			break;
		case 63:
			
			break;
		case 66:
			
			break;
		case 91930134:
			player.addItem(new Item(962));
			player.handleDonation(25, "Christmas cracker");
			player.sendMessage("You've purchased a: ["+Colors.red+"Christmas Cracker</col>].");
			break;
		case 91930135:
			player.addItem(new Item(1050));
			player.handleDonation(20, "Red Santa Hat");
			player.sendMessage("You've purchased a: ["+Colors.red+"Red Santa Hat</col>].");
			break;
		case 91930136:
			player.addItem(new Item(30412));
			player.handleDonation(20, "Black Santa Hat");
			player.sendMessage("You've purchased a: ["+Colors.red+"Black Santa Hat</col>].");
			break;
		case 91930137:
			player.addItem(new Item(1053));
			player.handleDonation(15, "Green H'ween Mask");
			player.sendMessage("You've purchased a: ["+Colors.red+"Green H'ween Mask</col>].");
			break;
		case 91930138:
			player.addItem(new Item(1057));
			player.handleDonation(15, "Red H'ween Mask");
			player.sendMessage("You've purchased a: ["+Colors.red+"Red H'ween Mask</col>].");
			break;
		case 91930139:
			player.addItem(new Item(1055));
			player.handleDonation(15, "Blue H'ween Mask");
			player.sendMessage("You've purchased a: ["+Colors.red+"Blue H'ween Mask</col>].");
			break;
		case 91930140:
			player.addItem(new Item(1037));
			player.handleDonation(10, "Bunny Ears");
			player.sendMessage("You've purchased: ["+Colors.red+"Bunny Ears</col>].");
			break;
		case 91930141:
			player.addItem(new Item(33625));
			player.handleDonation(10, "Christmas Scythe");
			player.sendMessage("You've purchased a: ["+Colors.red+"Christmas Scythe</col>].");
			break;
		case 91930142:
			player.addItem(new Item(33522));
			player.handleDonation(10, "Teddy Bear");
			player.sendMessage("You've purchased a: ["+Colors.red+"Teddy Bear</col>].");
			break;
		}
	}
	
	public StoreManager(Player player) {
		this.player = player;
	}
}
