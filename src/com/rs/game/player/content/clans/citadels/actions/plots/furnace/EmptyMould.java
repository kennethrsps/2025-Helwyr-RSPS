package com.rs.game.player.content.clans.citadels.actions.plots.furnace;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.clans.citadels.ClanCitadel;
import com.rs.game.player.content.clans.citadels.actions.plots.PlotStage;
import com.rs.game.player.content.clans.citadels.actions.plots.furnace.WaterTrough.TroughConstants;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class EmptyMould extends Action {

	public static Animation PLAYER_ANIMATION  = new Animation(6367);
	public int respawnDelay = 160;
	
	public enum MouldConstants {
		
		STAGE_1(26343, 26380),
		STAGE_2(26380, 26381),
		STAGE_3(26381, 26336);
		
		public int currentObj;
		public int nextObj;
		
		
		MouldConstants(int currentObj, int nextObj) {
			this.currentObj = currentObj;
			this.nextObj = nextObj;
		}
		
		public int getCurrentObject() {
			return currentObj;
		}
		
		public int getNextObject() {
			return nextObj;
		}
		
	}
	
	private WorldObject object;
	private MouldConstants constants;
	
	public EmptyMould(WorldObject object, MouldConstants constants) {
		this.object = object;
		this.constants = constants;
	}
	
	public WorldObject getObject() {
		return object;
	}
	
	public MouldConstants getConstants() {
		return constants;
	}

	@Override
	public boolean start(Player player) {
		if(!checkAll(player)) {
			return false;
		}
		setActionDelay(player, 1);
		return true;
	}
	
	public boolean checkAll(Player player) {
		if(player.getSkills().getLevel(Skills.SMITHING) < 15) {
			player.getPackets().sendGameMessage("You do not have the required level to interact with this object.");
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
		PlotStage stage = player.getClanManager().getClan().getPlotStage();
		ClanCitadel citadel = player.getClanManager().getClan().getClanCitadel();
		player.unlock();
		player.setNextAnimation(PLAYER_ANIMATION);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				long time = respawnDelay * 600;
				if(object.getRotation() == 0) {
					World.spawnTemporaryObject(new WorldObject(constants.getNextObject(), 10, 0, citadel.getBoundChuncks()[0] + 97, citadel.getBoundChuncks()[1] + 140, player
								.getPlane()), time * 600);
				} else {
					World.spawnTemporaryObject(new WorldObject(constants.getNextObject(), 10, 2, citadel.getBoundChuncks()[0] * 0 + 97, citadel.getBoundChuncks()[1] * 0 + 140, player
							.getPlane()), time * 600);
				}
				World.spawnTemporaryObject(new WorldObject(TroughConstants.GOLD_STAGE_1.getNextObject(), 10, 2, citadel.getBoundChuncks()[0] * 0 + 97, citadel.getBoundChuncks()[1] * 0 + 142, player
						.getPlane()), time * 600);
				player.getSkills().addXp(Skills.SMITHING, 170);
			}
		},  13);
		//player.setNextAnimationNoPriority(PLAYER_ANIMATION);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextAnimationNoPriority(PLAYER_ANIMATION);
				long time = respawnDelay * 600;
				if(object.getRotation() == 0) {
					World.spawnTemporaryObject(new WorldObject(MouldConstants.STAGE_2.getNextObject(), 10, 0, citadel.getBoundChuncks()[0] + 97, citadel.getBoundChuncks()[1] + 140, player
							.getPlane()), time * 600);
				} else {
					World.spawnTemporaryObject(new WorldObject(MouldConstants.STAGE_2.getNextObject(), 10, 2, citadel.getBoundChuncks()[0] * 0 + 97, citadel.getBoundChuncks()[1] * 0 + 140, player
									.getPlane()), time * 600);
				}
				World.spawnTemporaryObject(new WorldObject(TroughConstants.GOLD_STAGE_2.getNextObject(), 10, 2, citadel.getBoundChuncks()[0] * 0 + 97, citadel.getBoundChuncks()[1] * 0 + 142, player
						.getPlane()), time * 600);
				player.getSkills().addXp(Skills.SMITHING, 170);
			}
		},  23);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				long time = respawnDelay * 600;
				if(object.getRotation() == 0) {
					World.spawnTemporaryObject(new WorldObject(MouldConstants.STAGE_3.getNextObject(), 10, 0, citadel.getBoundChuncks()[0] + 97, citadel.getBoundChuncks()[1] + 140, player
							.getPlane()), time * 600);
				} else {
					World.spawnTemporaryObject(new WorldObject(MouldConstants.STAGE_3.getNextObject(), 10, 2, citadel.getBoundChuncks()[0] * 0 + 97, citadel.getBoundChuncks()[1] * 0 + 140, player
									.getPlane()), time * 600);
				}
				World.spawnTemporaryObject(new WorldObject(TroughConstants.GOLD_STAGE_3.getNextObject(), 10, 2, citadel.getBoundChuncks()[0] * 0 + 97, citadel.getBoundChuncks()[1] * 0 + 142, player
						.getPlane()), time * 600);
				player.getSkills().addXp(Skills.SMITHING, 170);
			}
		},  46);
		return -1;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 2);
	}

}