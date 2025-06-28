package com.rs.game.player.newquests.impl;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Magic;
import com.rs.game.player.newquests.Quest;
import com.rs.game.player.newquests.NewQuestManager.Progress;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class CooksAssistant extends Quest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1606373669258580526L;

	public static final int BUCKET_OF_MILK = 15413, POT_OF_FLOUR = 15414, EGG = 15412;

	public boolean[] givenItems = new boolean[3];

	@Override
	public void start(boolean accept) {
		player.closeInterfaces();
		if (accept) {
			progress = Progress.STARTED;
			sendConfigs();
		}
		player.getDialogueManager().startDialogue("CookDNew", 278, accept);
	}

	@Override
	public void update() {
		switch (stage) {
		case -1:
			sendQuestInformation(
					"<col=08088A>It's the <col=8A0808>Duke of Lumbridge's</col> <col=08088A>birthday and I have to help",
					"<col=08088A>his</col> <col=8A0808>Cook</col> <col=08088A>make him a <col=8A0808>birthday cake.</col><col=08088A> To do this I need to",
					"<col=08088A>bring him the following ingredients:",
					givenItems[0] ? "<str>I have given the cook a Top-quality milk.</str>"
							: ((player.getInventory().containsItem(BUCKET_OF_MILK, 1)
									|| player.getBank().containsItem(BUCKET_OF_MILK, 1))
											? "<col=08088A>I have found a <col=8A0808>Top-quality milk</col><col=08088A> to give to the cook."
											: "<col=08088A>A <col=8A0808>Top-quality milk</col><col=08088A>."),
					givenItems[1] ? "<str>I have given the cook an Extra fine flour.</str>"
							: ((player.getInventory().containsItem(POT_OF_FLOUR, 1)
									|| player.getBank().containsItem(POT_OF_FLOUR, 1))
											? "<col=08088A>I have found an <col=8A0808>Extra fine flour</col><col=08088A> to give to the cook."
											: "<col=08088A>An <col=8A0808>Extra fine flour</col><col=08088A>."),
					givenItems[2] ? "<str>I have given the cook a Super large egg.</str>"
							: ((player.getInventory().containsItem(EGG, 1) || player.getBank().containsItem(EGG, 1))
									? "<col=08088A>I have found a <col=8A0808>Super large egg</col><col=08088A> to give to the cook."
									: "<col=08088A>And <col=8A0808>a Super large egg</col><col=08088A>."));
			break;
		case 0:
			sendQuestInformation("<str>It was the Duke of Lumbridge's birthday,  but his cook had",
					"<str>forgotten to buy the ingredients he needed to make him a",
					"<str>cake. I brought the cook an egg, some flour and some milk",
					"<str>and then cook made a delicious looking cake with them.",
					"<str>As a reward he now lets me use his high quality range",
					"<str>which lets me burn things less whenever I wish to cook", "<str>there.",
					"<col=FF0000>QUEST COMPLETE!");
			break;
		}
	}

	@Override
	public void finish() {
		progress = Progress.COMPLETED;
		stage = 0;
		sendConfigs();
		sendRewards();
	}

	private Item[] rewardItems = new Item[] { new Item(326, 20), new Item(995, 500000) };

	@Override
	public void giveRewards() {
		player.getSkills().addXp(Skills.COOKING, 3000);
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
		player.getPackets().sendConfig(29, progress == Progress.STARTED ? 1 : progress == Progress.COMPLETED ? 2 : 0);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().sendRunScriptBlank(2165);
			}
		});
	}

	@Override
	public boolean hasRequirments() {
		return true;
	}

	@Override
	public String[] getQuestInformation() {
		return new String[] { "Cook's Assistance", "Not needed", "There aren't any requirements for this quest.",
				"not needed", "not needed",
				"1 Quests Point, 3000 Cooking XP, 500k coins, 20 sardines and access to the cook's range." };
	}

	@Override
	public int getQuestPoints() {
		return 1;
	}

	@Override
	public int questId() {
		return 1;
	}

	@Override
	public int getRewardItemId() {
		return 1891;
	}

	@Override
	public void sendQuestJournalInterface(boolean show) {
		player.getInterfaceManager().sendInterface(1243);
		player.getPackets().sendGlobalConfig(699, 512);
		player.getPackets().sendGlobalString(359, getQuestInformation()[2]);// requirments
		if (show) {
			player.getPackets().sendHideIComponent(1243, 45, false);
			player.getPackets().sendHideIComponent(1243, 56, true);
			player.getPackets().sendHideIComponent(1243, 57, true);
		}
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 278) {// CookDNew
			player.getDialogueManager().startDialogue("Cook", npc.getId(), true);
			return false;
		}
		return true;
	}

	@Override
	public boolean canTakeItem(FloorItem item) {
		if (item.getId() == CooksAssistant.EGG) {
			if (progress == Progress.COMPLETED || progress == Progress.NOT_STARTED) {
				player.getPackets().sendGameMessage("Nothing intresting happens.");
				return false;
			}
			if (givenItems[2] || player.getInventory().containsItem(CooksAssistant.EGG, 1)
					|| player.getBank().containsItem(CooksAssistant.EGG, 1)) {
				player.getPackets().sendGameMessage("The cook only needs one egg.");
				return false;
			}
			World.removeGroundItem(player, item);
			return false;
		}
		return true;
	}

	@Override
	public void teleportToStartPoint() {
		Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3208, 3213, 0));
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 47721) {
			if (progress == Progress.COMPLETED || progress == Progress.NOT_STARTED) {
				player.getPackets().sendGameMessage("You cant milk that cow.");
				return false;
			}
			if (givenItems[2] || player.getInventory().containsItem(CooksAssistant.BUCKET_OF_MILK, 1)
					|| player.getBank().containsItem(CooksAssistant.BUCKET_OF_MILK, 1)) {
				player.getPackets().sendGameMessage("The cant have more than 1 bucket of fine milk.");
				return false;
			}
			if (!player.getInventory().containsItem(1925, 1)) {
				player.getPackets().sendGameMessage("You need an empty bucket to milk the cow.");
				return false;
			}
			player.setNextAnimation(new Animation(2305));
			player.getInventory().deleteItem(1925, 1);
			player.getInventory().addItem(15413, 1);
			player.lock(1);
			player.getPackets().sendGameMessage("You milk the cow.");
			return false;
		}
		return true;
	}

}
