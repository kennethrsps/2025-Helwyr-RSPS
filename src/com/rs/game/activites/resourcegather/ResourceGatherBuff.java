package com.rs.game.activites.resourcegather;

import java.io.Serializable;
import java.util.HashMap;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

public class ResourceGatherBuff implements Serializable {
	private static final long serialVersionUID = -1099151572657415057L;
	
	/**
	 * INTERS:
	 * 919
	 */
	
	private Player player;
	private HashMap<Integer, Integer> resourceMap;//skillers resource buff
	private HashMap<Integer, ResourceObject> resourceMapTemp;//chat box statistics
	
	private double totalXpGain;
	private int totalCollected;
	public ResourceObject lastResourceViewed;
	
	public ResourceGatherBuff(Player player) {
		this.player = player;
		this.resourceMap = new HashMap<Integer, Integer>();
		this.resourceMapTemp = new HashMap<Integer, ResourceObject>();
	}
	
	public void sendChatInter(ResourceObject resource) {
		if(resource == null) {
			if(resourceMapTemp.isEmpty())
				return;
			resource = lastResourceViewed;
		}
		if(resource == null)
			return;
		if(!player.getInterfaceManager().containsChatBoxInter())
			player.getInterfaceManager().sendChatBoxInterface(919);
		player.getPackets().sendIComponentText(919, 31, "Reset statistics");
		player.getPackets().sendIComponentText(919, 65, "Total XP Gain:");
		player.getPackets().sendIComponentText(919, 75, ""+ResourceObject.getFormattedStat((int) getTotalTempXP()));
		player.getPackets().sendIComponentText(919, 66, "Total GP Gain:");
		player.getPackets().sendIComponentText(919, 76, ""+ResourceObject.getFormattedStat((int) getTotalTempGP()));
		player.getPackets().sendIComponentText(919, 62, resource.isNpc ? NPCDefinitions.getNPCDefinitions(resource.itemId).name : ItemDefinitions.getItemDefinitions(resource.itemId).getName());
		player.getPackets().sendIComponentText(919, 69, "Amount: <br>"+ResourceObject.getFormattedStat(resource.amount));
		player.getPackets().sendIComponentText(919, 68, "P/H: <br>"+ResourceObject.getFormattedStat(resource.getAmountPerHour()));
		player.getPackets().sendIComponentText(919, 70, "XP Gain: <br>"+ResourceObject.getFormattedStat((int)player.getSkills().getModifiedXP(resource.skillId, resource.xpGain)));
		player.getPackets().sendIComponentText(919, 71, "P/H: <br>"+ResourceObject.getFormattedStat((int)player.getSkills().getModifiedXP(resource.skillId, resource.getXPGainPerHour())));
		player.getPackets().sendIComponentText(919, 72, "GP Gain: <br>"+ResourceObject.getFormattedStat(resource.getTotalValue()));
		player.getPackets().sendIComponentText(919, 73, "P/H: <br>"+ResourceObject.getFormattedStat(resource.getGPGainPerHour()));
		player.getPackets().sendIComponentText(919, 74, "");
		player.getPackets().sendIComponentText(919, 67, "");
		player.getPackets().sendIComponentText(919, 77, "");
	}
	
	public double getTotalTempXP() {
		double xp = 0;
		for(ResourceObject res : resourceMapTemp.values()) {
			xp += player.getSkills().getModifiedXP(res.skillId, res.xpGain);
		}
		return xp;
	}
	
	public double getTotalTempGP() {
		int gp = 0;
		for(ResourceObject res : resourceMapTemp.values()) {
			gp += res.getTotalValue();
		}
		return gp;
	}
	
	public void submitResourceGatherForStats(Item item, double xp, int skillId, boolean isNpc) {
		int id = item.getId();
		ResourceObject resource = null;
		if(resourceMapTemp.containsKey(id)) {
			resource = resourceMapTemp.get(id);
			resource.amount += item.getAmount();
			resource.xpGain += xp;
			resourceMapTemp.put(id, resource);
		} else {
			resource = new ResourceObject(id, item.getAmount(), xp, Utils.currentTimeMillis());
			resource.isNpc = isNpc;
			resource.timeStampStarted = Utils.currentTimeMillis();
			resource.skillId = skillId;
			resourceMapTemp.put(id, resource);
		}
		lastResourceViewed = resource;
		if(player.getInterfaceManager().containsChatBoxInter())
			player.getResourceGather().sendChatInter(resource);
		submitSkillEventResources(item, xp, skillId, isNpc);
		if(!isNpc)
			submitResourceGather(item);
		//player.getDailyQuests().addResourceGather(item.getId());
	}
	
	public void submitSkillEventResources(Item item, double xp, int skillId, boolean isNpc) {
		int id = item.getId();
		ResourceObject resource = null;
		SkillingEventSkillObject skillEvent = SkillingEventsManager.eventObjects.get(skillId);
		if(skillEvent == null) {
			return;
		}
		SkillingEventObject playerEvent = skillEvent.getPlayerEvent(player.getUsername());
		if(playerEvent == null) {
			SkillingEventObject newEvent = new SkillingEventObject(player.getUsername());
			skillEvent.eventList.add(newEvent);
		}
		playerEvent = skillEvent.getPlayerEvent(player.getUsername());
		if(playerEvent == null)
			return;
		if(playerEvent.resources.containsKey(id)) {
			resource = playerEvent.resources.get(id);
			resource.amount += item.getAmount();
			resource.xpGain += xp;
			playerEvent.resources.put(id, resource);
		} else {
			resource = new ResourceObject(id, item.getAmount(), xp, Utils.currentTimeMillis());
			resource.isNpc = isNpc;
			playerEvent.resources.put(id, resource);
		}
	}
	
	public static final String[] VALID_EXTRA_ITEMS = {" log", " ore", "flax", "grimy ", "shrimp", "herring", "trout", "pike", "tuna", "sea turtle", "bass", "swordfish", "salmon", "cavefish", "lobster", "shark", "monkfish", "manta ray", "rocktail"};
	
	public void submitResourceGather(Item item) {
		//player.getInventory().addItem(item);
		boolean valid = false;
		String itemName = item.getDefinitions().getName().toLowerCase();
		for(String s : VALID_EXTRA_ITEMS) {
			if(itemName.contains(s) || itemName.equals(s)) {
				valid = true;
				break;
			}
		}
		if(!valid) {
			return;
		}
		/*if(player.getAccountType() != StarterTutorial.SKILLER) {
			return;
		}*/
		if(resourceMap.containsKey(item.getId())) {
			int percent = resourceMap.get(item.getId()).intValue();
			percent += 10;//10% gain
			if(percent >= 100) {
				player.succeedMessage("Your skiller profile type has gained you an extra resource gather!");
				player.getInventory().addItem(item.getId(), 1);
				resourceMap.remove(item.getId());
			} else
				resourceMap.put(item.getId(), percent);
		} else {
			resourceMap.put(item.getId(), 10);
		}
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public HashMap<Integer, Integer> getResourceMap() {
		return resourceMap;
	}

	public void setResourceMap(HashMap<Integer, Integer> resourceMap) {
		this.resourceMap = resourceMap;
	}

	public HashMap<Integer, ResourceObject> getResourceMapTemp() {
		return resourceMapTemp;
	}

	public void setResourceMapTemp(HashMap<Integer, ResourceObject> resourceMapTemp) {
		this.resourceMapTemp = resourceMapTemp;
	}

	public double getTotalXpGain() {
		return totalXpGain;
	}

	public void setTotalXpGain(double totalXpGain) {
		this.totalXpGain = totalXpGain;
	}

	public int getTotalCollected() {
		return totalCollected;
	}

	public void setTotalCollected(int totalCollected) {
		this.totalCollected = totalCollected;
	}

}
