package com.rs.game.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.player.actions.Fishing;
import com.rs.game.player.actions.Fishing.Fish;
import com.rs.game.player.actions.Woodcutting.TreeDefinitions;
import com.rs.game.player.actions.crafting.LeatherCrafting.LeatherData;
import com.rs.game.player.actions.mining.Mining.RockDefinitions;
import com.rs.game.player.actions.smithing.Smelting.SmeltingBar;
import com.rs.game.player.actions.smithing.Smithing.ForgingBar;
import com.rs.game.player.actions.smithing.Smithing.ForgingInterface;
import com.rs.utils.Utils;

public class DailyTaskManager implements Serializable {

	private static final long serialVersionUID = -2162393007501800660L;
	private static int[] AMOUNTS = { 200, 250, 275, 250, 40, 45, 50 };

	private DailyTask currentTask;
	private transient Player player;
	private int amountLeft;
	private boolean filterMessage, claimedReward;
	private long doubleXpTime;
	private int productId;

	public void process(int productId) {
		process(productId, 1);
	}

	public void process(int productId, int amount) {
		if (currentTask == null)
			return;
		if (player.getDailyTaskManager().getProductId() != productId)
			return;
		amountLeft -= amount == 1 ? getReduceAmount() : amount;
		if (amountLeft <= 0) {
			amountLeft = 0;
			finishTask();
		} else if (!player.getDailyTaskManager().isFilterMessage())
			player.getPackets()
					.sendGameMessage("<col=00ff00>Daily task status has been updated. You currently have "
							+ player.getDailyTaskManager().getAmountLeft() + " "
							+ ItemDefinitions.getItemDefinitions(productId).getName().toLowerCase() + " left to "
							+ currentTask.toString() + ".");
	}

	public void getNewTask(boolean force) {
		if (!force && currentTask != null)
			return;
		currentTask = DailyTask.values()[Utils.random(DailyTask.values().length)];
		claimedReward = false;
		if (doubleXpTime > 0)
			doubleXpTime = 0;
		currentTask.init(player);
		amountLeft = AMOUNTS[Utils.random(currentTask == null ? 4 : 0, currentTask == null ? 6 : 3)]
				* getReduceAmount();
		player.getPackets().sendGameMessage("You have recived new daily task!");
		player.getPackets().sendGameMessage("Your daily task is: " + currentTask.getTaskMessage(player) + "; only "
				+ player.getDailyTaskManager().getAmountLeft() + " more to go.");
	}

	private int getReduceAmount() {
		String name = ItemDefinitions.getItemDefinitions(productId).getName().toLowerCase();
		return (name.contains("dart tip") || name.contains("bolts (unf)") ? 20
				: name.contains("arrowheads") || name.contains("nails") ? 30 : 1);
	}

	public void finishTask() {
		amountLeft = 0;
		currentTask = null;
		player.getBank().addItem(11640, 1, true);
		player.getPackets().sendGameMessage(
				"You have completed your daily task! Talk to Martin Steelweaver at home for your reward.");
		if (!player.getInventory().addItem(11640, 1)) {
			player.getBank().addItem(11640, 1, true);
			player.getPackets().sendGameMessage("You have received 1 vote token, it has been sent to your bank.");
			return;
		}
		player.getPackets().sendGameMessage("You have received 1 vote token, it has been added to your inventory.");
	}

	public DailyTask getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(DailyTask currentTask) {
		this.currentTask = currentTask;
	}

	public boolean isFilterMessage() {
		return filterMessage;
	}

	public void setFilterMessage(boolean filterMessage) {
		this.filterMessage = filterMessage;
	}

	public int getAmountLeft() {
		return amountLeft;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean HasClaimedReward() {
		return claimedReward;
	}

	public void setClaimedReward(boolean claimedReward) {
		this.claimedReward = claimedReward;
	}

	public boolean canClaimReward() {
		return amountLeft == 0 && currentTask == null && !claimedReward;
	}

	public void activateDoubleXp() {
		doubleXpTime = Utils.currentTimeMillis() + (2 * 60 * 60 * 1000);
		claimedReward = true;
	}

	public String getDoubleXpTimeLeft() {
		long doubleXpTimeLeft = doubleXpTime - Utils.currentTimeMillis();
		long seconds = doubleXpTimeLeft / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		return ((hours % 24) + " hours, " + (minutes % 60) + " minutes , and " + (seconds % 60) + " seconds");
	}

	public boolean hasDoubleXpActivated() {
		return doubleXpTime > Utils.currentTimeMillis();
	}

	public long getDoubleXpTime() {
		return doubleXpTime;
	}

	public void setDoubleXpTime(long doubleXpTime) {
		this.doubleXpTime = doubleXpTime;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public enum DailyTask implements Serializable {
		FISHING_TASK() {
			public void init(Player player) {
				List<Integer> availableIds = new ArrayList<Integer>();
				for (Fish fish : Fishing.Fish.values()) {
					if (player.getSkills().getLevelForXp(Skills.FISHING) >= fish.getLevel())
						availableIds.add(fish.getId());
				}
				player.getDailyTaskManager().setProductId(availableIds.get(Utils.random(availableIds.size() - 1)));
			}

			public String getTaskMessage(Player player) {
				return "catch " + ItemDefinitions.getItemDefinitions(player.getDailyTaskManager().getProductId())
						.getName().toLowerCase() + "";
			}

			public String toString() {
				return "catch";
			}
		},
		MINING_TASK() {
			public void init(Player player) {
				List<Integer> availableIds = new ArrayList<Integer>();
				for (RockDefinitions rock : RockDefinitions.values()) {
					if (rock == RockDefinitions.Granite_Ore || rock == RockDefinitions.Sandstone_Ore)
						continue;
					if (player.getSkills().getLevelForXp(Skills.MINING) >= rock.getLevel())
						availableIds.add(rock.getOreId());
				}
				player.getDailyTaskManager().setProductId(availableIds.get(Utils.random(availableIds.size() - 1)));
			}

			public String getTaskMessage(Player player) {
				return "Mine " + ItemDefinitions.getItemDefinitions(player.getDailyTaskManager().getProductId())
						.getName().toLowerCase() + "";
			}

			public String toString() {
				return "mine";
			}
		},
		SMITHING_TASK() {
			public void init(Player player) {
				List<Integer> availableIds = new ArrayList<Integer>();
				for (ForgingBar forgingBar : ForgingBar.values()) {
					for (int i = 0; i < forgingBar.getItems().length; i++) {
						Item item = forgingBar.getItems()[i];
						if (item == null || item.getId() == -1)
							continue;
						if (player.getSkills().getLevelForXp(Skills.SMITHING) >= ForgingInterface.getLevels(forgingBar,
								i, player))
							availableIds.add(forgingBar.getItems()[i].getId());
					}
				}
				player.getDailyTaskManager().setProductId(availableIds.get(Utils.random(availableIds.size() - 1)));
			}

			public String getTaskMessage(Player player) {
				return "Smith " + ItemDefinitions.getItemDefinitions(player.getDailyTaskManager().getProductId())
						.getName().toLowerCase() + "";
			}

			public String toString() {
				return "smith";
			}
		},
		SMELTING_TASK() {
			public void init(Player player) {
				List<Integer> availableIds = new ArrayList<Integer>();
				for (SmeltingBar smeltingBar : SmeltingBar.values()) {
					if (smeltingBar == SmeltingBar.CORRUPTED_ORE)
						continue;
					Item item = smeltingBar.getProducedBar();
					if (item == null || item.getId() == -1)
						continue;
					if (player.getSkills().getLevelForXp(Skills.SMITHING) >= smeltingBar.getLevelRequired())
						availableIds.add(item.getId());
				}
				player.getDailyTaskManager().setProductId(availableIds.get(Utils.random(availableIds.size() - 1)));
			}

			public String getTaskMessage(Player player) {
				return "Smelt " + ItemDefinitions.getItemDefinitions(player.getDailyTaskManager().getProductId())
						.getName().toLowerCase() + "";
			}

			public String toString() {
				return "smelt";
			}
		},
		WOODCUTTING_TASK() {
			public void init(Player player) {
				List<Integer> availableIds = new ArrayList<Integer>();
				for (TreeDefinitions treeDefinitions : TreeDefinitions.values()) {
					int itemId = treeDefinitions.getLogsId();
					if (treeDefinitions.ordinal() > TreeDefinitions.MAGIC.ordinal() || itemId == -1)
						continue;
					if (player.getSkills().getLevelForXp(Skills.WOODCUTTING) >= treeDefinitions.getLevel())
						availableIds.add(itemId);
				}
				player.getDailyTaskManager().setProductId(availableIds.get(Utils.random(availableIds.size() - 1)));
			}

			public String getTaskMessage(Player player) {
				return "Chop " + ItemDefinitions.getItemDefinitions(player.getDailyTaskManager().getProductId())
						.getName().toLowerCase() + "";
			}

			public String toString() {
				return "chop";
			}
		},
		CRAFTING_TASK() {
			public void init(Player player) {
				List<Integer> availableIds = new ArrayList<Integer>();
				for (LeatherData leatherData : LeatherData.values()) {
					int itemId = leatherData.getFinalProduct();
					if (player.getSkills().getLevelForXp(Skills.CRAFTING) >= leatherData.getRequiredLevel())
						availableIds.add(itemId);
				}
				player.getDailyTaskManager().setProductId(availableIds.get(Utils.random(availableIds.size() - 1)));
			}

			public String getTaskMessage(Player player) {
				return "Craft " + ItemDefinitions.getItemDefinitions(player.getDailyTaskManager().getProductId())
						.getName().toLowerCase() + "";
			}

			public String toString() {
				return "craft";
			}
		},
		RUNECRAFTING_TASK() {
			public void init(Player player) {
				List<Integer> availableIds = new ArrayList<Integer>();
				int[][] runes = { { 556, 558, 555, 557, 554, 559, 564, 562, 9075, 561, 563, 560, 565 },
						{ 1, 2, 5, 9, 14, 20, 27, 35, 40, 45, 50, 65, 77 } };
				for (int i = 0; i < runes[0].length; i++) {
					if (player.getSkills().getLevelForXp(Skills.RUNECRAFTING) >= runes[1][i])
						availableIds.add(runes[0][i]);
				}
				player.getDailyTaskManager().setProductId(availableIds.get(Utils.random(availableIds.size() - 1)));
			}

			public String getTaskMessage(Player player) {
				return "Craft " + ItemDefinitions.getItemDefinitions(player.getDailyTaskManager().getProductId())
						.getName().toLowerCase() + "";
			}

			public String toString() {
				return "craft";
			}
		};

		public void init(Player player) {

		}

		public String getTaskMessage(Player player) {
			return "";
		}

		private DailyTask() {

		}

	}

}
