package com.rs.game.player.content;

import com.rs.game.WorldObject;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.newquests.NewQuestManager.Progress;
import com.rs.game.player.newquests.impl.CooksAssistant;

public class SawMills {

	public static int EMPTY_POT = 1931, POT_OF_FLOUR = 1933, WHEAT = 1947;

	public enum Mill {
		LUMBRIDGE(70034, 2718, 36880);

		private int hooperId;
		private int hooperControlId;
		private int millObjectId;

		private Mill(int hooperId, int hooperControlId, int millObjectId) {
			this.hooperId = hooperId;
			this.hooperControlId = hooperControlId;
			this.millObjectId = millObjectId;
		}

		public int getHooperId() {
			return hooperId;
		}

		public int getHooperControlId() {
			return hooperControlId;
		}

		public int getMillObjectId() {
			return millObjectId;
		}

		public static Mill getMill(WorldObject object) {
			for (Mill mill : Mill.values()) {
				if (object.getId() == mill.getHooperId() || object.getId() == mill.getHooperControlId()
						|| object.getId() == mill.getMillObjectId())
					return mill;
			}
			return null;
		}
	}

	public static boolean isMilling(Player player, WorldObject object, Item item) {
		Mill mill = Mill.getMill(object);
		if (mill == null)
			return false;
		if (item != null) {
			if (item.getId() == EMPTY_POT) {
				if (object.getId() != mill.getMillObjectId())
					return false;
				if (player.getSawMillProgress() == 0)
					return false;
				item.setId(hasSpecialFlour(player, object.getId()) ? CooksAssistant.POT_OF_FLOUR : POT_OF_FLOUR);
				player.getInventory().refresh();
				player.getPackets().sendGameMessage("You fill the pot with flour.");
				player.decreaseSawMillProgress();
			} else if (item.getId() == WHEAT) {
				if (object.getId() != mill.getHooperId())
					return false;
				if (player.HasWheatInHooper()) {
					player.getPackets().sendGameMessage("There is already wheat in the hooper press the control.");
					return true;
				}
				player.getInventory().deleteItem(item);
				player.setHasWheatInHooper(true);
				player.getPackets().sendGameMessage("You put some wheat in the hooper.");
			} else
				return false;
		} else {
			if (object.getId() == mill.getMillObjectId()) {
				if (!player.getInventory().containsItem(EMPTY_POT, 1)) {
					player.getPackets().sendGameMessage("You need to have an empty pot.");
					return true;
				}
				if (player.getSawMillProgress() == 0)
					return false;
				player.getInventory().deleteItem(new Item(EMPTY_POT, 1));
				player.getInventory().addItem(new Item(
						hasSpecialFlour(player, object.getId()) ? CooksAssistant.POT_OF_FLOUR : POT_OF_FLOUR, 1));
				player.getPackets().sendGameMessage("You fill the pot with flour.");
				player.decreaseSawMillProgress();
			} else if (object.getId() == mill.getHooperControlId()) {
				if (!player.HasWheatInHooper()) {
					player.getPackets().sendGameMessage("Put wheat in the hooper before operating.");
					return true;
				}
				player.setHasWheatInHooper(false);
				player.increaseSawMillProgress();
				player.getPackets().sendGameMessage("You operate the controler.");
			} else if (object.getId() == mill.getHooperId()) {
				player.getPackets().sendGameMessage(
						(!player.HasWheatInHooper()) ? "The hooper is empty." : "The hooper has wheat.");
			} else
				return false;
		}
		return true;
	}

	private static boolean hasSpecialFlour(Player player, int objectId) {
		CooksAssistant quest = (CooksAssistant) player.getNewQuestManager().getQuests().get(1);
		return objectId == 36880 && !player.getNewQuestManager().hasCompletedQuest(1)
				&& player.getNewQuestManager().getProgress(1) == Progress.STARTED && !quest.givenItems[1]
				&& !player.getBank().containsItem(CooksAssistant.POT_OF_FLOUR, 1)
				&& !player.getInventory().containsItem(CooksAssistant.POT_OF_FLOUR, 1);
	}
}
