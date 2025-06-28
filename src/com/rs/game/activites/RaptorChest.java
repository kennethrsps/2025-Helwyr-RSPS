package com.rs.game.activites;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * A class used to handle the Raptor Chest.
 * @author Kingkenobi
 */
public class RaptorChest {

    /**
     * An Array holding all the keyparts.
     */
    public static Item[] KEYPARTS = { new Item(36001), new Item(36012), new Item(36042) };

    /**
     * Item holding the Raptor Key.
     */
    private static Item RKEY = new Item(36066);

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
    
    //new Item(386, Utils.random(10, 100)),
    private static Item[] COMMON = { new Item(4151, 1), new Item(4153, 1), new Item(11235, 1),  new Item(15486, 1),
	        new Item(6568, 1), new Item(18831, 125),
	        //barrow reward
	        new Item(4708, 1), new Item(4710, 1), new Item(4712, 1), new Item(4714, 1),
			new Item(4716, 1), new Item(4718, 1), new Item(4720, 1), new Item(4722, 1), new Item(4724, 1),
			new Item(4726, 1), new Item(4728, 1), new Item(4730, 1), new Item(4732, 1), new Item(4734, 1),
			new Item(4736, 1), new Item(4738, 1), new Item(4745, 1), new Item(4747, 1), new Item(4749, 1),
			new Item(4751, 1), new Item(4753, 1), new Item(4755, 1), new Item(4757, 1), new Item(25652, 1),
			new Item(25672, 1)    
	        };

    private static Item[] UNCOMMON = { new Item(4152, 2), new Item(4154, 2), new Item(11236, 2),  new Item(15487, 2), 
    new Item(29863, 2), new Item(6571, 2), new Item(24512, 1), new Item(30746, 1), new Item(30747, 1), new Item(30748, 1),   
    new Item(36066, 1)
            };

    private static Item[] RARE = {new Item(29863, 4), new Item(36004, 1), new Item(36008, 1), new Item(35985, 1),
    		new Item(34151, 1),	new Item(34153, 1), new Item(34156, 1), new Item(31237, 1), new Item(31238, 1),
    		new Item(31239, 1), new Item(31240, 1), new Item(31241, 1), new Item(31242, 1), new Item(31243, 1),
    		new Item(31237, 1), new Item(31232, 1), new Item(31233, 1), new Item(31234, 1), new Item(30062, 1),
    		new Item(30065, 1), new Item(36066, 2)
    		
            };

    private static Item[] VERY_RARE = {new Item(29863, 6), new Item(36035, 1), new Item(36036, 1), new Item(36037, 1),
    		new Item(37195, 1), new Item(39046, 1), new Item(38520, 1), new Item(41450, 1), new Item(41449, 1)
    		};

    /**
     * Handles the reward.
     * @param player The player.
     */
    public static void addRaptorReward(Player player) {
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
     * Makes the Raptor Key
     * 
     * @param player
     *            The Player.
     */

    public static void makeKey(Player player) {
    	if (player.containsOneItem(36001) && player.containsOneItem(36012) && player.containsOneItem(36042)) {
	    	player.getInventory().removeItems(KEYPARTS);
	    	player.getInventory().addItem(RKEY);
	    	player.sendMessage("You bound the keyparts together and make a " + RKEY.getName().toLowerCase() + ".");
    	} else
    		player.sendMessage("You'll need both key halves in order to assemble the Raptor key.");
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
		if (player.getInventory().containsItem(36066, 1) && !player.isLocked()) {
//			player.getAchManager().addKeyAmount("Raptor", 1);
		    player.faceObject(object);
		    player.lock(2);
		    player.getInventory().deleteItem(RKEY);
		    player.setNextAnimation(CHEST_EMOTE);
		    player.sendMessage("You unlock the chest with your key..", true); 
		    player.getPackets().sendSound(soundId[0], soundId[1], soundId[2]);
		    World.sendWorldMessage(Colors.red + "<shad=000000><img=6>News: "
					+ player.getDisplayName() + " Received a reward from RAPTOR Chest!", false);
		    WorldTasksManager.schedule(new WorldTask() {
		    	
		    	@Override
		    	public void run() {
		    		addRaptorReward(player);
		    		player.incrementChestsOpened();
		    		player.sendMessage("You find some treasure in the chest; chests opened: "+
		    				Colors.red+Utils.getFormattedNumber(player.getChestsOpened())+"</col>.", true);
		    		if (player.getPerkManager().keyExpert)
			    		addRaptorReward(player);
		    		player.unlock();
		    		this.stop();
		    	}
		    }, 1);
		} else if (!player.getInventory().containsItem(36066, 1))
		    player.getDialogueManager().startDialogue("SimpleMessage", "You need a Raptor key to open this chest.");
	}
    
    /**
	 * Opens the interface of all obtainable rewards from the chest.
	 * @param player The player to send the interface to.
	 */
	public static void openRewardsInterface(Player player) {
		player.getInterfaceManager().sendInterface(275);
		for (int i = 0; i < 310; i++)
			player.getPackets().sendIComponentText(275, i, "");
		player.getPackets().sendIComponentText(275, 1, "Raptor Chest rewards</u>");
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