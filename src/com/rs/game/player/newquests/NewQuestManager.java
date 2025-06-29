package com.rs.game.player.newquests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.rs.Settings;
import com.rs.game.WorldObject;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

/**
 * 
 * @author ARMAR X K1NG
 */
public class NewQuestManager implements Serializable {

	private static final long serialVersionUID = 7526433940646726231L;
	private transient Player player;
	private boolean filter;
	private int catagories;
	private ArrayList<Quest> quests;
	private transient Quest current;

	/**
	 * Construct a new {@code QuestManager} {@code Object}.
	 * 
	 * @param player
	 */
	public NewQuestManager() {
		quests = new ArrayList<Quest>(Settings.QUESTS);
		for (int i = 0; i <= Settings.QUESTS; i++) {
			quests.add(i, null);
		}
	}

	/**
	 * Start a new quest.
	 * 
	 * @param quest
	 *            The quest key.
	 * @param parameters
	 *            The parameters.
	 */
	public void startQuest(int questId, Object... parameters) {
		Quest quest = quests.get(questId);
		if (quest == null)
			return;
		if (!quest.hasRequirments())
			return;
		this.current = quest;
		quest.parameters = parameters;
		quest.sendQuestJournalInterface(true);
	}

	public Progress getProgress(int questId) {
		Quest quest = quests.get(questId);
		if (quest == null)
			return Progress.NOT_STARTED;
		return quest.progress;
	}

	/**
	 * Initialize the quest manager.
	 */
	public void initialize() {
		for (int i = 0; i <= Settings.QUESTS; i++) {
			Quest questInstance = Quests.getQuest(i);
			if (questInstance != null) {
				questInstance.player = player;
				quests.set(i, (quests.get(i) == null) ? questInstance : quests.get(i));
			}
		}
		player.getPackets().sendUnlockIComponentOptionSlots(190, 38, 0, 2, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(190, 15, 0, Settings.QUESTS, 0, 1, 2, 3);
		player.getPackets().sendUnlockIComponentOptionSlots(190, 3, 0, 2, 0);
		player.getPackets().sendConfig(904, 333);
		player.getPackets().sendConfig(101, player.getQuestPoints());
		player.getPackets().sendGlobalConfig(699, 951);
		refreshQuests();
	}

	/**
	 * Refresh the progress status of all quests in the quest tab.
	 */
	public void refreshQuests() {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			quest.sendConfigs();
		}
		refreshQuestsInterface();
	}

	public boolean hasCompletedQuest(int questId) {
		Quest quest = quests.get(questId);
		if (quest == null)
			return false;
		return quest.progress == Progress.COMPLETED;
	}

	public void sendQuestJournalInterface(int questId) {
		Quest quest = quests.get(questId);
		if (quest == null) {
			player.getPackets().sendGameMessage("Quest is not added yet.");
			return;
		}
		if (getProgress(questId) == Progress.NOT_STARTED) {
			quest.sendQuestJournalInterface(false);
		} else {
			quest.update();
		}
	}

	/**
	 * Get the quests.
	 * 
	 * @return The array list holding all quests, completed, started and
	 *         not-started.
	 */
	public ArrayList<Quest> getQuests() {
		return quests;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void toggleFilter() {
		filter = !filter;
		refreshQuestsInterface();
	}

	public void setCatagories(int catagories) {
		this.catagories = catagories;
		refreshQuestsInterface();
	}

	private void refreshQuestsInterface() {
		player.getPackets().sendConfig(1384, filter ? (catagories == 0 ? 0 : catagories == 1 ? 45 : 80)
				: (catagories == 0 ? 514 : catagories == 1 ? 545 : 580));
		player.getPackets().sendRunScriptBlank(2165);
	}

	public boolean processNPCClick1(NPC npc) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.processNPCClick1(npc))
				return false;
		}
		return true;
	}

	public boolean processNPCClick2(NPC npc) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.processNPCClick2(npc))
				return false;
		}
		return true;
	}

	public boolean processNPCClick3(NPC npc) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.processNPCClick3(npc))
				return false;
		}
		return true;
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.processItemOnNPC(npc, item))
				return false;
		}
		return true;
	}

	public boolean processObjectClick1(WorldObject object) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.processObjectClick1(object))
				return false;
		}
		return true;
	}

	public boolean processObjectClick2(WorldObject object) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.processObjectClick2(object))
				return false;
		}
		return true;
	}

	public boolean processObjectClick3(WorldObject object) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.processObjectClick3(object))
				return false;
		}
		return true;
	}

	public boolean processObjectClick4(WorldObject object) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.processObjectClick4(object))
				return false;
		}
		return true;
	}

	public boolean processObjectClick5(WorldObject object) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.processObjectClick5(object))
				return false;
		}
		return true;
	}

	public boolean handleItemOnObject(WorldObject object, Item item) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.handleItemOnObject(object, item))
				return false;
		}
		return true;
	}

	public boolean canTakeItem(FloorItem item) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.canTakeItem(item))
				return false;
		}
		return true;
	}

	public boolean canDropItem(Item item) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.canDropItem(item))
				return false;
		}
		return true;
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.canUseItemOnItem(itemUsed, usedWith))
				return false;
		}
		return true;
	}

	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.processButtonClick(interfaceId, componentId, slotId, slotId2, packetId))
				return false;
		}
		return true;
	}

	public boolean useDig() {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.useDig())
				return false;
		}
		return true;
	}

	public boolean handleObjectExamine(WorldObject object) {
		for (Quest quest : quests) {
			if (quest == null)
				continue;
			if (!quest.handleObjectExamine(object))
				return false;
		}
		return true;
	}

	public void setQuests(ArrayList<Quest> quests) {
		this.quests = quests;
	}

	public Quest getCurrent() {
		return current;
	}

	public void setCurrent(Quest current) {
		this.current = current;
	}

	public static enum Progress {
		COMPLETED, STARTED, NOT_STARTED;
	}

	public static class Quests {

		private static final Map<Integer, Class<Quest>> quests = new HashMap<Integer, Class<Quest>>(Settings.QUESTS);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static final void init() {
			try {
				Class[] classes = Utils.getClasses("com.rs.game.player.newquests.impl");
				for (Class c : classes) {
					if (c.isAnonymousClass())
						continue;
					Quest script = (Quest) c.newInstance();
					if (script == null)
						continue;
					int key = script.questId();
					quests.put(key, c);
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}

		public static Quest getQuest(int questId) {
			Class<Quest> quest = quests.get(questId);
			if (quest == null) {
				return null;
			}
			try {
				return quest.newInstance();
			} catch (Throwable e) {
				Logger.handle(e);
			}
			return null;
		}

		static {

		}
	}
}