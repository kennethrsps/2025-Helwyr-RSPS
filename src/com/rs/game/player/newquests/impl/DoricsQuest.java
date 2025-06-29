package com.rs.game.player.newquests.impl;

import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Magic;
import com.rs.game.player.newquests.Quest;
import com.rs.game.player.newquests.NewQuestManager.Progress;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class DoricsQuest extends Quest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5892481909516737812L;

	@Override
	public void start(boolean accept) {
		player.closeInterfaces();
		if (accept) {
			progress = Progress.STARTED;
			sendConfigs();
		}
		player.getDialogueManager().startDialogue("DoricsD", 284, accept);
	}

	@Override
	public void update() {
		switch (stage) {
		case -1:
			sendQuestInformation("<str>I have spoken to <col=8A0808>Doric</str>",
					BLUE + "I need to collect some items and bring them to " + RED + "Doric",
					player.getInventory().containsItem(434, 6) ? "<str>6 Clay</str>" : RED + "6 Clay",
					player.getInventory().containsItem(436, 4) ? "<str>4 Copper Ore</str>" : RED + "4 Copper Ore",
					player.getInventory().containsItem(440, 2) ? "<str>2 Iron Ore</str>" : RED + "2 Iron Ore");
			break;
		case 0:
			sendQuestInformation("<str>I have spoken to <col=8A0808>Doric</str>",
					"<str> I have collected some Clay, Copper Ore, and Iron Ore",
					"<str>Doric rewarded me for all my hard work", "<str>I can now use Doric's Anvils whenever I want",
					"<col=FF0000>QUEST COMPLETE!");
			break;
		}
	}

	@Override
	public void finish() {
		stage = 0;
		progress = Progress.COMPLETED;
		sendConfigs();
		sendRewards();
	}

	private Item[] rewardItems = new Item[] { new Item(995, 500000) };

	@Override
	public void giveRewards() {
		player.getSkills().addXp(Skills.MINING, 13000);
		for (int i = 0; i < rewardItems.length; i++) {
			Item item = rewardItems[i];
			if (item == null)
				continue;
			if (!player.getInventory().addItemMoneyPouch(item)) {
				player.getBank().addItem(item.getId(), item.getAmount(), true);
				player.getPackets().sendGameMessage(item.getName() + " has been added to your bank.");
			}
		}
	}

	@Override
	public void sendConfigs() {
		player.getPackets().sendConfig(31, progress == Progress.STARTED ? 1 : progress == Progress.COMPLETED ? 100 : 0);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().sendRunScriptBlank(2165);
			}
		});
	}

	public void sendQuestJournalInterface(boolean show) {
		player.getInterfaceManager().sendInterface(1243);
		player.getPackets().sendGlobalConfig(699, 514);
		player.getPackets().sendIComponentText(1243, 19, getQuestInformation()[1]);// Start
																					// point
		player.getPackets().sendGlobalString(359, getQuestInformation()[2]);// requirments
		player.getPackets().sendIComponentText(1243, 25, getQuestInformation()[3]);// Required
																					// Items
		player.getPackets().sendIComponentText(1243, 34, getQuestInformation()[4]);// Combat
		player.getPackets().sendIComponentText(1243, 37, getQuestInformation()[5]);// Rewards
		if (show) {
			player.getPackets().sendHideIComponent(1243, 45, false);
			player.getPackets().sendHideIComponent(1243, 56, true);
			player.getPackets().sendHideIComponent(1243, 57, true);
		}
	}

	@Override
	public boolean hasRequirments() {
		return true;
	}

	@Override
	public String[] getQuestInformation() {
		return new String[] { "Doric's Quest",
				("I can start this quest by speaking to " + "Doric " + "who is " + "North of " + "Falador."),
				("There aren't any requirements but " + "Level 15 Mining " + "will help"), "None.", "None.",
				"1 Quests Point, 13000 Mining XP, 500k coins, Use of doric's Anvil." };
	}

	@Override
	public int getQuestPoints() {
		return 1;
	}

	@Override
	public int questId() {
		return 3;
	}

	@Override
	public int getRewardItemId() {
		return 1269;
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 284) {// Doric
			player.getDialogueManager().startDialogue("DoricsD", npc.getId());
			return false;
		}
		return true;
	}

	@Override
	public void teleportToStartPoint() {
		Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2951, 3439, 0));
	}
}
