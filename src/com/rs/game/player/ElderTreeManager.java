package com.rs.game.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.WorldObject;
import com.rs.game.player.actions.Woodcutting;
import com.rs.utils.Utils;

public class ElderTreeManager implements Serializable {

	private static final long serialVersionUID = 6216983590682053969L;

	public static int DEPELETE_CONSTANT = 300000, RENEW_CONSTANT = 600000;
	public static int[] ELDER_TREES = { 87512, 87514, 87516, 87518, 87520, 87522, 87524, 87526, 87528, 87530 };

	private List<ElderTree> elderTrees;
	private transient Player player;

	public ElderTreeManager() {
		elderTrees = new ArrayList<ElderTree>(10);
	}

	public void init() {
		for (ElderTree tree : elderTrees)
			tree.refresh();
	}

	public void process() {
		elderTrees.removeIf(e -> e.remove);
		for (ElderTree tree : elderTrees)
			tree.process();
	}

	public boolean isElderTree(WorldObject object) {
		for (int id : ELDER_TREES)
			if (id == object.getId()) {
				ElderTree tree = getElderTree(id);
				if (tree == null)
					tree = new ElderTree(id);
				player.getActionManager().setAction(new Woodcutting(tree));
				return true;
			}
		return false;
	}

	public ElderTree getElderTree(int objectId) {
		for (ElderTree tree : elderTrees)
			if (tree.objectId == objectId)
				return tree;
		return null;
	}

	public class ElderTree implements Serializable {

		private static final long serialVersionUID = -8268240666175725168L;
		private int objectId;
		private int varbitId;
		private boolean depleted;
		private boolean remove;
		private boolean started;
		private long cycleTime;

		public ElderTree(int objectId) {
			this.objectId = objectId;
			this.varbitId = ObjectDefinitions.getObjectDefinitions(objectId).configFileId;
			cycleTime = Utils.currentTimeMillis();
		}

		public void start() {
			if (!started) {
				started = true;
				renewCycle();
				elderTrees.add(this);
			}
		}

		public void process() {
			if (cycleTime == 0 || remove)
				return;
			if (cycleTime < Utils.currentTimeMillis()) {
				depleted = !depleted;
				refresh();
				if (depleted)
					renewCycle();
				else
					remove();
			}
		}

		private void remove() {
			cycleTime = 0;
			remove = true;
		}

		private void renewCycle() {
			cycleTime += depleted ? RENEW_CONSTANT : DEPELETE_CONSTANT;
		}

		@SuppressWarnings("unused")
		private void resetCycle() {
			cycleTime = Utils.currentTimeMillis();
		}

		public void refresh() {
			player.getPackets().sendConfigByFile(varbitId, 0);
		}

		public boolean isDepleted() {
			return depleted;
		}

		public void setDepleted(boolean depleted) {
			this.depleted = depleted;
		}

		public long getCycleTime() {
			return cycleTime;
		}

		public void setCycleTime(long cycleTime) {
			this.cycleTime = cycleTime;
		}

		public int getObjectId() {
			return objectId;
		}

	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
