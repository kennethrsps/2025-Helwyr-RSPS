package com.rs.game.activites;

import com.rs.game.Animation;
import com.rs.game.WorldObject;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * A class used to handle the Crystal Chest.
 * @author Zeus
 */
public class CrystalChest {

    /**
     * An Array holding all the keyparts.
     */
    public static Item[] KEYPARTS = { new Item(985), new Item(987) };

    /**
     * Item holding the Crystal Key.
     */
    private static Item KEY = new Item(989);

    /**
     * Animation, the Chest Animation.
     */
    private static Animation CHEST_EMOTE = new Animation(536);

    /**
     * Int[] the Sound ID.
     */
    private static int[] soundId = { 52, 0, 1 };

    /**
     * Item array holding all the rewards.
     */
    private static Item[] rewards;

    /**
     * Item rewards.
     */
    private static Item[] COMMON = { new Item(386, Utils.random(10, 100)),
	    new Item(1127, 1), new Item(892, Utils.random(10, 100)), new Item(557, Utils.random(10, 100)),
	    new Item(559, Utils.random(10, 100)), new Item(558, Utils.random(10, 100)), 
	    new Item(1337, 1), new Item(3202, 1), new Item(6288, Utils.random(6, 20)), 
	    new Item(562, Utils.random(10, 100)), new Item(560, Utils.random(10, 100)),
	    new Item(563, Utils.random(10, 100)), new Item(564, Utils.random(10, 100)), new Item(1289, 1),
	    new Item(995, Utils.random(50000, 1000000)), new Item(439, Utils.random(50, 70)),
	    new Item(437, Utils.random(50, 70)), new Item(441, Utils.random(30, 55)),
	    new Item(454, Utils.random(35, 60)), new Item(445, Utils.random(25, 50)),
	    new Item(443, Utils.random(30, 50)), new Item(448, Utils.random(20, 40)),
	    new Item(450, Utils.random(15, 35)), new Item(452, Utils.random(15, 30)),
	    new Item(1512, Utils.random(50, 100)), new Item(1514, Utils.random(15, 30)),
	    new Item(1516, Utils.random(20, 40)), new Item(1518, Utils.random(30, 70)),
	    new Item(1520, Utils.random(40, 80)), new Item(1522, Utils.random(50, 90)),
	    new Item(1149), new Item(10828), new Item(1187), new Item(1215), new Item(1249),
	    new Item(1305), new Item(1377), new Item(1434), new Item(3204), new Item(4087),
	    new Item(4585), new Item(4587) };

    private static Item[] UNCOMMON = { new Item(4153, 1), new Item(10564), new Item(10589),
	    new Item(4587, 1), new Item(1127, 1), new Item(5698, 1), new Item(14679),
	    new Item(6522, Utils.random(10, 100)), new Item(537, Utils.random(10, 15)),
	    new Item(995, Utils.random(50000, 150000)), new Item(1093, 1), new Item(6524, 1),
	    new Item(4091, 1), new Item(4093, 1), new Item(6288, Utils.random(6, 25)), new Item(4089, 1),
	    new Item(2890), new Item(9729), new Item(9731), new Item(9733), new Item(3122), new Item(6809),
	    new Item(6548), new Item(6541), new Item(3327), new Item(3329), new Item(3331), new Item(3333),
	    new Item(3335), new Item(3337), new Item(3339), new Item(3341), new Item(3343), new Item(2368),
	    new Item(2366), new Item(2368), new Item(6547), new Item(6619), new Item(6621), new Item(6623),
	    new Item(6625), new Item(6627), new Item(6629), new Item(6631), new Item(6633), new Item(6180),
	    new Item(6181), new Item(6182), new Item(6184), new Item(6185), new Item(6186), new Item(6187),
	    new Item(6188), new Item(7592), new Item(7593), new Item(7594), new Item(7595), new Item(7596),
	    new Item(6654), new Item(6655), new Item(6656), new Item(18699), new Item(20448), new Item(20458),
	    new Item(18697), new Item(20460), new Item(20450), new Item(18693), new Item(18695), new Item(18691),
	    new Item(20462), new Item(20452)};

    private static Item[] RARE = { new Item(6914, 1), new Item(20440), new Item(20444), new Item(20436),
	    new Item(6889, 1), new Item(2629, 1), new Item(20464), new Item(20454), new Item(20442),
	    new Item(18831, Utils.random(1, 3)), new Item(1187, 1), new Item(20446), new Item(20438),
	    new Item(1149, 1), new Item(1514, Utils.random(10, 15)), new Item(20456), new Item(20466),
	    new Item(3101, 1), new Item(563, Utils.random(10, 250)), 
	    new Item(564, Utils.random(10, 250)), new Item(7158), new Item(11732),
	    new Item(995, Utils.random(50000, 200000)), new Item(4151)};

    private static Item[] VERY_RARE = { new Item(4860), new Item(4866),
    		new Item(4860), new Item(4872), 
    		 new Item(4878), new Item(4932), new Item(4938), 
    		 new Item(4944), new Item(4950), new Item(4740, Utils.random(15, 50)),
    		 new Item(4956), new Item(4962), new Item(4968), 
    		 new Item(4974), new Item(4980), new Item(4986),
    		 new Item(4992), new Item(4998), new Item(4908), 
    		 new Item(4914), new Item(4920), new Item(4926),
    		 new Item(4884), new Item(4890), new Item(4896), 
    		 new Item(4902), new Item(21742), new Item(21750),
    		 new Item(21758), new Item(21766), new Item(995, Utils.random(50000, 250000)),
    		 new Item(32380), new Item(32383), new Item(32386), new Item(32389),
    		 new Item(31392), new Item(31393), new Item(31394), new Item(31395)};

    /**
     * Handles the reward.
     * @param player The player.
     */
    public static void addCrystalReward(Player player) {
		int commonRewards = Utils.random(50), uncommonRewards = Utils.random(100), rareRewards = Utils.random(200);
		if (uncommonRewards > 65 && rareRewards > 100)
		    rewards = new Item[] { RARE[Utils.random(RARE.length)] };
		else if (commonRewards > 15 && uncommonRewards > 50)
		    rewards = new Item[] { UNCOMMON[Utils.random(UNCOMMON.length)] };
		else if (commonRewards < 40 && uncommonRewards > 40)
		    rewards = new Item[] { COMMON[Utils.random(COMMON.length)] };
		else
		    rewards = new Item[] { COMMON[Utils.random(COMMON.length)] };
		if (Utils.random(300) > 275)
		    rewards = new Item[] { VERY_RARE[Utils.random(VERY_RARE.length)] };
		for (Item item : rewards) {
			if (item.getId() == 995) {
				player.addMoney(item.getAmount());
				return;
			}
			player.addItem(item);
		}
    }

    /**
     * Makes the Crystal Key
     * 
     * @param player
     *            The Player.
     */

    public static void makeKey(Player player) {
    	if (player.containsOneItem(985) && player.containsOneItem(987)) {
	    	player.getInventory().removeItems(KEYPARTS);
	    	player.getInventory().addItem(KEY);
	    	player.sendMessage("You bound the keyparts together and make a " + KEY.getName().toLowerCase() + ".");
    	} else
    		player.sendMessage("You'll need both key halves in order to assemble the key.");
    }

    /**
     * Opens the chest
     * 
     * @param object
     *            The Chest.
     * @param player
     *            The Player.
     */
    public static void openChest(WorldObject object, final Player player) {
		if (player.getInventory().containsItem(989, 1) && !player.isLocked()) {
			player.getAchManager().addKeyAmount("Crystal", 1);
		    player.faceObject(object);
		    player.lock(2);
		    player.getInventory().deleteItem(KEY);
		    player.setNextAnimation(CHEST_EMOTE);
		    player.sendMessage("You unlock the chest with your key..", true); 
		    player.getPackets().sendSound(soundId[0], soundId[1], soundId[2]);
		    WorldTasksManager.schedule(new WorldTask() {
		    	@Override
		    	public void run() {
		    		addCrystalReward(player);
		    		player.incrementChestsOpened();
		    		player.sendMessage("You find some treasure in the chest; chests opened: "+
		    				Colors.red+Utils.getFormattedNumber(player.getChestsOpened())+"</col>.", true);
		    		if (player.getPerkManager().keyExpert)
			    		addCrystalReward(player);
		    		player.unlock();
		    		this.stop();
		    	}
		    }, 1);
		} else if (!player.getInventory().containsItem(989, 1))
		    player.getDialogueManager().startDialogue("SimpleMessage", "You need a crystal key to open this chest.");
	}
    
    /**
	 * Opens the interface of all obtainable rewards from the chest.
	 * @param player The player to send the interface to.
	 */
	public static void openRewardsInterface(Player player) {
		player.getInterfaceManager().sendInterface(275);
		for (int i = 0; i < 310; i++)
			player.getPackets().sendIComponentText(275, i, "");
		player.getPackets().sendIComponentText(275, 1, "Crystal Chest rewards</u>");
		player.getPackets().sendIComponentText(275, 10, "Coins: x 50'000 - 200'000");
		int number = 0;
		for (Item reward : COMMON) {
			if (reward.getId() == 995)
				continue;
			String name = reward.getName() + (reward.getDefinitions().isNoted() ? " - noted" : "");
			player.getPackets().sendIComponentText(275, 11 + number, ""+name);
			number++;
		}
		for (Item reward : UNCOMMON) {
			if (reward.getId() == 995)
				continue;
			String name = reward.getName() + (reward.getDefinitions().isNoted() ? " - noted" : "");
			player.getPackets().sendIComponentText(275, number + 11, ""+name);
			number++;
		}
		for (Item reward : RARE) {
			if (reward.getId() == 995)
				continue;
			String name = reward.getName() + (reward.getDefinitions().isNoted() ? " - noted" : "");
			player.getPackets().sendIComponentText(275, number + 11, ""+name);
			number++;
		}
		for (Item reward : VERY_RARE) {
			if (reward.getId() == 995)
				continue;
			String name = reward.getName() + (reward.getDefinitions().isNoted() ? " - noted" : "");
			player.getPackets().sendIComponentText(275, number + 11, ""+name);
			number++;
		}
	}
}