package com.rs.game.player.content.miscellania;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.player.Player;
import com.rs.game.player.content.miscellania.resources.ResourceIncome;
import com.rs.game.player.content.miscellania.resources.StashType;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public class ThroneOfMiscellania implements Serializable {

	private static final long serialVersionUID = 5012748557430549224L;

	public static final int WOOD = 0, HERBS = 1, FISHING = 2, MINING = 3, IDLE = 4, HARD_WOOD = 5, FARM = 6;

	private static final int[] STAGE_VARS = { 81 /*Wood*/, 82 /*Herbs*/, 83 /*Fishing*/, 84 /*Mining*/, -1 /*Idle*/, 2131 /*Hard wood*/, 2132 /*Farm*/ };

	private ItemsContainer<Item> resources;
	private boolean herbEnabled, cookedFishEnabled, hasVisited;
	private boolean mahogany, teak, both;
	private int[] barStages, followers, subjects;
	private double reputation;
	private int cofferAmount, woodType;
	private Player player;


	/**
	 * @Initalizes new instance of <ThroneOfMiscellania>
	 * @Constructor
	 */
	public ThroneOfMiscellania() {
		resources = new ItemsContainer<Item>(50, false);
		subjects = new int[4];
		barStages = new int[STAGE_VARS.length];
		barStages[IDLE] = 15;
	}

	/**
	 * Initalizes the objects and properties of the <Throne>
	 */
	public void init() {
		if(resources == null) 
			resources = new ItemsContainer<Item>(50, false);
		if(subjects == null)
			subjects = new int[4];
		if (barStages == null)
			barStages = new int[STAGE_VARS.length];
		startTimer();
	}

	/**
	 * Interfaces handles which followers on on which plot.
	 */
	public void displayInterface() {
		player.getVarsManager().sendVarBit(2140, 30);// enables the other two bars
		player.getInterfaceManager().sendInterface(391);
		refreshCoffer();
		refreshAllBars();
	}

	public void refreshWoodType() {
		player.getVarsManager().sendVarBit(2133,  woodType);
	}

	private void refreshHerbSetting() {
		player.getVarsManager().sendVarBit(2134, herbEnabled ? 0 : 1);
	}

	private void refreshCookedFish() {
		player.getVarsManager().sendVarBit(135, cookedFishEnabled ? 1 : 0);
	}

	private void refreshAllBars() {
		for (int bar = 0; bar < barStages.length; bar++)
			updateStage(bar);
	}

	private void updateStage(int bar) {
		int varBit = STAGE_VARS[bar];
		if (varBit == -1) {
			player.getVarsManager().sendVar(1888, barStages[bar]);
			return;
		}
		player.getVarsManager().sendVarBit(varBit == -1 ? 1888 : varBit, barStages[bar]); 
	}

	private void refreshCoffer() {
		player.getVarsManager().sendVarBit(74, cofferAmount);
	}

	/**
	 * Initalizes the hourly resource stash. <Timer>
	 */
	private void startTimer() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if(!hasVisited) {
					double oldReputation =  reputation;
					addReputation(getReputation() - 25);
					if(reputation < 0.0)
						reputation = 0.0;
					if(reputation > 25) {
						player.getPackets().sendGameMessage("<img=5><col=ff0000>Your reputation with your kingdom has been decreased by 25%");
					} else {
						if(reputation <= 0.0 && oldReputation > 0.1)
							player.getPackets().sendGameMessage("<img=5><col=ff0000>You no longer have any reputation with your kingdom.");
					}
				}
				new ResourceIncome(player, StashType.RANDOM);
				hasVisited = false;
			}

		}, 1, 1, TimeUnit.HOURS);
	}

	public void handleKingdomInterface(int componentId) {
		switch(componentId) {
		case 155://herbs
			herbEnabled = true;
			refreshHerbSetting();
			break;
		case 157://flax
			herbEnabled = false;
			refreshHerbSetting();
			break;
		case 143://farm up
			increaseStock(FARM);
			break;
		case 144://farm down
			decreaseStock(FARM);
			break;
		case 134://mahogany hard wood
			break;
		case 135://teak wood
			break;
		case 136://all hard wood
			break;
		case 122://hardwood up
			increaseStock(HARD_WOOD);
			break;
		case 123://hardwood down
			decreaseStock(HARD_WOOD);
			break;
		case 120://fishing/cooked
			if(cookedFishEnabled)
				cookedFishEnabled = false;
			else
				cookedFishEnabled = true;
			refreshCookedFish();
			break;
		case 68://wood up
			increaseStock(WOOD);
			break;
		case 69://wood down
			decreaseStock(WOOD);
			break;
		case 55://herbs up
			increaseStock(HERBS);
			break;
		case 56://herbs down
			decreaseStock(HERBS);
			break;
		case 41://mining up
			increaseStock(MINING);
			break;
		case 43://mining down
			decreaseStock(MINING);
			break;
		case 85://fishing up
			increaseStock(FISHING);
			break;
		case 86://fishing down
			decreaseStock(FISHING);
			break;
		}
	}
	
	public void decreaseStock(int type) {
		if(barStages[type] == 0) //doesnt send negative varbit
			return;
		barStages[type] -= 1;
		barStages[IDLE] += 1;
		refreshAllBars();
	}
	
	public void increaseStock(int type) {
		if(barStages[IDLE] == 0) {
			player.getPackets().sendGameMessage("You dont have enough followers.");
			return;
		}
		if(barStages[type] > 9)  {
			player.getPackets().sendGameMessage("You have too many followers on this resource.");
			return;
		}
		barStages[type] += 1;
		barStages[IDLE] -= 1;
		refreshAllBars();
	}

	/**
	 * Handles @Resources <Interface><ComponentId>
	 * @param componentId
	 * @param slotId
	 * @param packetId
	 */
	public void handleCollectedResources(int componentId, int slotId, int packetId) {
		switch (componentId) {
		case 8:
			player.getBank().addItems(resources.toArray(), false);
			resources.clear();
			player.getPackets().sendGameMessage("All the items were moved to your bank.");
			break;
		case 9:
			resources.clear();
			player.getPackets().sendGameMessage("All the items were removed from the chest.");
			break;
		case 10:
			for (int slot = 0; slot < resources.toArray().length; slot++) {
				Item item = resources.get(slot);
				if (item == null) {
					continue;
				}
				boolean added = true;
				if (item.getDefinitions().isStackable() || item.getAmount() < 2) {
					added = player.getInventory().addItem(item);
					if (added) {
						resources.toArray()[slot] = null;
					}
				} else {
					for (int i = 0; i < item.getAmount(); i++) {
						Item single = new Item(item.getId());
						if (!player.getInventory().addItem(single)) {
							added = false;
							break;
						}
						resources.remove(single);
					}
				}
				if (!added) {
					player.getPackets().sendGameMessage(
							"You only had enough space in your inventory to accept some of the items.");
					break;
				}
			}
			break;
		case 7:
			Item item = resources.get(slotId);
			if (item == null) 
				return;
			switch (packetId) {
			case 52:
				player.getPackets().sendGameMessage("It's a " + item.getDefinitions().getName());
				return;
			case 4:
				resources.toArray()[slotId] = null;
				break;
			case 64:
				player.getBank().addItems(new Item[] { resources.toArray()[slotId] }, false);
				resources.toArray()[slotId] = null;
				break;
			case 61:
				boolean added = true;
				if (item.getDefinitions().isStackable() || item.getAmount() < 2) {
					added = player.getInventory().addItem(item);
					if (added) {
						resources.toArray()[slotId] = null;
					}
				} else {
					for (int i = 0; i < item.getAmount(); i++) {
						Item single = new Item(item.getId());
						if (!player.getInventory().addItem(single)) {
							added = false;
							break;
						}
						resources.remove(single);
					}
				}
				if (!added) {
					player.getPackets().sendGameMessage(
							"You only had enough space in your inventory to accept some of the items.");
					break;
				}
				break;
			}
		}
		openResources();
	}

	/**
	 * Opens <Interface> for collected @Resources
	 */
	public void openResources() {
		player.getInterfaceManager().sendInterface(1284);
		player.getPackets().sendIComponentText(1284, 28, "Resources Collected");
		player.getPackets().sendInterSetItemsOptionsScript(1284, 7, 100, 8, 3);
		player.getPackets().sendUnlockIComponentOptionSlots(1284, 7, 0, 10, 0, 1, 2, 3);
		player.getPackets().sendItems(100, resources);
	}


	/**
	 * Adds the resource selected <Item> to the players resource stash.
	 * @param item
	 * @return
	 */
	public boolean addResource(Item item) {
		return resources.add(item);
	}

	public double getReputation() {
		if(reputation > 100.0)
			reputation = 100.0;
		return reputation;
	}

	public void addReputation(double reputation) {
		if(reputation >= 100.0)
			return;
		double oldReputation = this.reputation;
		this.reputation += reputation;
		if(this.reputation > 100.0)
			this.reputation = 100.0;
		if(oldReputation < 100 && this.reputation >= 100.0)
			;//TODO add finished dialogue
	}

	public void lostReputation(double reputation) {
		if(reputation >= 100.0)
			return;
		this.reputation -= reputation;
		if(this.reputation < 0.0)
			this.reputation = 0.0;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public ItemsContainer<Item> getStash() {
		return resources;
	}

	public int[] getSubjects() {
		return subjects;
	}

	public int getSubject(int index) {
		return subjects[index];
	}

	public int setSubject(int index, int amount) {
		return subjects[index] = amount;
	}

	public void setSubjects(int[] subjects) {
		this.subjects = subjects;
	}

	public int[] getFollowers() {
		return followers;
	}

	public int getAmountOfFollowers() {
		return followers.length;
	}

	public int getFollowers(int index) {
		return followers[index];
	}

	public void setFollowers(int[] followers) {
		this.followers = followers;
	}

	public int getCofferAmount() {
		return cofferAmount;
	}

	public void setCofferAmount(int cofferAmount) {
		this.cofferAmount = cofferAmount;
	}

	public boolean isHasVisited() {
		return hasVisited;
	}

	public void setHasVisited(boolean hasVisited) {
		this.hasVisited = hasVisited;
	}

	public int setBarStage(int barType, int amount) {
		return barStages[barType] = amount;
	}
	
	public static boolean isInArea(Player player) {
		switch(player.getRegionId()) {
		case 10044:
		case 10301:
		case 10300:
			return true;
		}
		return false;
	}

}
