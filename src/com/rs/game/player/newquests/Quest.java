package com.rs.game.player.newquests;

import java.io.Serializable;

import com.rs.game.WorldObject;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.newquests.NewQuestManager.Progress;

/**
 * @author ARMAR X K1NG
 */
public abstract class Quest implements Serializable {

	private static final long serialVersionUID = 2587188148765693378L;

	public Player player;

	public Object[] parameters;

	public int stage = -1;

	public String RED = "<col=8A0808>";

	public String BLUE = "<col=08088A>";

	public Progress progress = Progress.NOT_STARTED;

	public abstract void start(boolean accept);

	public abstract void update();

	public void sendRewards() {
		player.setQuestPoints(player.getQuestPoints() + getQuestPoints());
		player.getInterfaceManager().sendInterface(1244);
		player.getPackets().sendIComponentText(1244, 25, "You have completed " + getQuestInformation()[0] + ".");
		player.getPackets().sendIComponentText(1244, 27, "Quest Points: " + player.getQuestPoints());
		player.getPackets().sendGlobalString(359, getQuestInformation()[5]);
		player.getPackets().sendItemOnIComponent(1244, 24, getRewardItemId(), 1);
		player.getPackets().sendConfig(101, player.getQuestPoints());
		player.getPackets()
				.sendGameMessage("Congratulations! You have completed the " + getQuestInformation()[0] + " quest!");
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				giveRewards();
			}
		});
	}

	public abstract void sendQuestJournalInterface(boolean show);

	public void sendQuestInformation(String... information) {
		player.getInterfaceManager().sendInterface(1245);
		player.getPackets().sendRunScript(4017, new Object[] { information.length + 2 });
		for (int i = 0; i < 331; i++)
			player.getPackets().sendIComponentText(1245, i, "");
		player.getPackets().sendIComponentText(1245, 330, getQuestInformation()[0]);
		for (int i = 0; i < information.length; i++)
			player.getPackets().sendIComponentText(1245, (i + 13), information[i]);
	}

	public abstract void finish();

	public abstract void giveRewards();

	public abstract void teleportToStartPoint();

	public abstract void sendConfigs();

	public abstract boolean hasRequirments();

	public abstract String[] getQuestInformation();

	public abstract int getQuestPoints();

	public abstract int questId();

	public abstract int getRewardItemId();

	public void setStage(int stage) {
		this.stage = stage;
	}

	public int getStage() {
		return this.stage;
	}

	public boolean processNPCClick1(NPC npc) {
		return true;
	}

	public boolean processNPCClick2(NPC npc) {
		return true;
	}

	public boolean processNPCClick3(NPC npc) {
		return true;
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		return true;
	}

	public boolean processObjectClick1(WorldObject object) {
		return true;
	}

	public boolean processObjectClick2(WorldObject object) {
		return true;
	}

	public boolean processObjectClick3(WorldObject object) {
		return true;
	}

	public boolean processObjectClick4(WorldObject object) {
		return true;
	}

	public boolean processObjectClick5(WorldObject object) {
		return true;
	}

	public boolean handleItemOnObject(WorldObject object, Item item) {
		return true;
	}

	public boolean canTakeItem(FloorItem item) {
		return true;
	}

	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		return true;
	}

	public boolean canDropItem(Item item) {
		return true;
	}

	public boolean useDig() {
		return true;
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		return true;
	}

	public boolean handleObjectExamine(WorldObject object) {
		return true;
	}
}