package com.rs.game.player.content.clans.citadels.actions.plots;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class Loom extends Action {


	public static Animation PLAYER_ANIMATION = new Animation(5562);
	public int respawnDelay = 160;
	
	public enum LoomConstants {

		STAGE_1(15335, 15338, new Animation(5684)),
		STAGE_2(15338, 15339, new Animation(5678)),
		STAGE_3(15339, 15340, new Animation(5621)),
		STAGE_4(15340, 15341, new Animation(5600));

		private int currentObj;
		private int nextObj;
		private Animation currentAnim;

		LoomConstants(int currentObj, int nextObj, Animation currentAnim) {
			this.currentObj = currentObj;
			this.nextObj = nextObj;
			this.currentAnim = currentAnim;
		}

		public int getCurrentObject() {
			return currentObj;
		}

		public int getNextObject() {
			return nextObj;
		}

		public Animation getCurrentAnimation() {
			return currentAnim;
		}

	}

	private WorldObject loom;
	private LoomConstants constants;


	public Loom(WorldObject loom, LoomConstants constants) {
		this.loom = loom;
		this.constants = constants;
	}
	
	public WorldObject getLoom() {
		return loom;
	}
	
	public LoomConstants getConstants() {
		return constants;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		setActionDelay(player, 1);
		return true;
	}


	public boolean checkAll(Player player) {
		if(player.getSkills().getLevel(Skills.CRAFTING) < 15) {
			player.getPackets().sendGameMessage("You do not have the required level to do this.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		player.setNextAnimationNoPriority(PLAYER_ANIMATION);
		player.getPackets().sendGameMessage("You begin to use the loom...");
		if(player.getX() == loom.getX() + 3) {
			player.getPackets().sendObjectAnimation(new WorldObject(constants.getNextObject(), 10, 0, player.getX() - 3, player.getY(), player.getPlane()), constants.getCurrentAnimation());
		} else {
			player.getPackets().sendObjectAnimation(new WorldObject(constants.getNextObject(), 10, 2, player.getX() + 1, player.getY(), player.getPlane()), constants.getCurrentAnimation());
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				long time = respawnDelay * 600;
				if(player.getX() == loom.getX() + 3) {
					World.spawnTemporaryObject(new WorldObject(constants.getNextObject(), 10, 0, player.getX() - 3, player.getY(), player.getPlane()), time * 600);
				} else {
					World.spawnTemporaryObject(new WorldObject(constants.getNextObject(), 10, 2, player.getX() + 1, player.getY(), player.getPlane()), time * 600);
				}
				player.getSkills().addXp(Skills.CRAFTING, 170);
				player.lock(2);
			}
		},  7);
		return -1;
	}
	

	@Override
	public void stop(Player player) {
		setActionDelay(player, 1);
	}

}
