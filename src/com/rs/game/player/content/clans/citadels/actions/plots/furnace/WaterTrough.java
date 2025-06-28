package com.rs.game.player.content.clans.citadels.actions.plots.furnace;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.clans.citadels.ClanCitadel;
import com.rs.game.player.content.clans.citadels.actions.plots.PlotStage;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class WaterTrough extends Action {
	
	public static Animation PLAYER_ANIMATION  = new Animation(6367);
	public static final int OPENED_HATCH = 26168;
	public static final int CLOSED_HATCH = 26157;
	public int respawnDelay = 160;

	public enum TroughConstants {
	
	SILVER_STAGE_1(26209, 26217),
	SILVER_STAGE_2(26217, 26212),
	SILVER_STAGE_3(26212, 26210),
	
	GOLD_STAGE_1(26209, 26335),
	GOLD_STAGE_2(26335, 26332),
	GOLD_STAGE_3(26332, 26331);
		
		/**
		 * Silver
		 */
		//26209 - empty
		//26210 - full
		//26212 - almost full
		//26217 - no where near full
		
		/**
		 * Gold
		 */
		//26331 full
		//26332 almost full
		//26335 - no where near full
		//26209 - empty
		
	public int currentObj;
	public int nextObj;
	
	
	TroughConstants(int currentObj, int nextObj) {
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
	private TroughConstants constants;
	
	public WaterTrough(WorldObject object, TroughConstants constants) {
		this.object = object;
		this.constants = constants;
	}
	
	public WorldObject getObject() {
		return object;
	}
	
	public TroughConstants getConstants() {
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
		long time = respawnDelay * 600;
		player.unlock();
		player.setNextAnimation(PLAYER_ANIMATION);
		World.spawnTemporaryObject(new WorldObject(OPENED_HATCH, 10, 0, citadel.getBoundChuncks()[0] * 0 + 99, citadel.getBoundChuncks()[1] * 0 + 143, player
				.getPlane()), time * 600);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if(object.getRotation() == 0) {
					World.spawnTemporaryObject(new WorldObject(TroughConstants.GOLD_STAGE_3.getCurrentObject(), 10, 0, citadel.getBoundChuncks()[0] + 97, citadel.getBoundChuncks()[1] + 142, player
								.getPlane()), time * 600);
				} else {
					World.spawnTemporaryObject(new WorldObject(TroughConstants.GOLD_STAGE_3.getCurrentObject(), 10, 2, citadel.getBoundChuncks()[0] * 0 + 97, citadel.getBoundChuncks()[1] * 0 + 142, player
							.getPlane()), time * 600);
				}
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
					World.spawnTemporaryObject(new WorldObject(TroughConstants.GOLD_STAGE_2.getCurrentObject(), 10, 0, citadel.getBoundChuncks()[0] + 97, citadel.getBoundChuncks()[1] + 142, player
							.getPlane()), time * 600);
				} else {
					World.spawnTemporaryObject(new WorldObject(TroughConstants.GOLD_STAGE_2.getCurrentObject(), 10, 2, citadel.getBoundChuncks()[0] * 0 + 97, citadel.getBoundChuncks()[1] * 0 + 142, player
									.getPlane()), time * 600);
				}
				player.getSkills().addXp(Skills.SMITHING, 170);
			}
		},  23);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				long time = respawnDelay * 600;
				if(object.getRotation() == 0) {
					World.spawnTemporaryObject(new WorldObject(TroughConstants.GOLD_STAGE_1.getCurrentObject(), 10, 0, citadel.getBoundChuncks()[0] + 97, citadel.getBoundChuncks()[1] + 142, player
							.getPlane()), time * 600);
				} else {
					World.spawnTemporaryObject(new WorldObject(TroughConstants.GOLD_STAGE_1.getCurrentObject(), 10, 2, citadel.getBoundChuncks()[0] * 0 + 97, citadel.getBoundChuncks()[1] * 0 + 142, player
									.getPlane()), time * 600);
				}
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