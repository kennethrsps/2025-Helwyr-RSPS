package com.rs.game.player.content.clans.citadels.actions.plots.furnace;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.clans.citadels.ClanCitadel;
import com.rs.game.player.content.clans.citadels.actions.plots.PlotStage;
import com.rs.game.player.content.clans.citadels.actions.plots.furnace.EmptyMould.MouldConstants;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class Shoveling extends Action {

	
	public static Animation PLAYER_ANIMATION  = new Animation(6631);
	public int respawnDelay = 160;
	
	public enum ShovelConstants {
		
		STAGE_1(26450, 26451),
		STAGE_2(26451, 26452),
		STAGE_3(26452, 26453);
		
		public int currentObj;
		public int nextObj;
		
		
		ShovelConstants(int currentObj, int nextObj) {
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
	private ShovelConstants constants;
	
	public Shoveling(WorldObject object, ShovelConstants constants) {
		this.object = object;
		this.constants = constants;
	}
	
	public WorldObject getObject() {
		return object;
	}
	
	public ShovelConstants getConstants() {
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
		player.lock();
		player.setNextAnimationNoPriority(PLAYER_ANIMATION);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				long time = respawnDelay * 600;
				if(object.getRotation() == 0) {
					World.spawnTemporaryObject(new WorldObject(constants.getNextObject(), 10, 0, player.getX() - 1, player.getY() - 1, player
								.getPlane()), time * 600);
				} else {
					World.spawnTemporaryObject(new WorldObject(constants.getNextObject(), 10, 2, player.getX() + 1, player.getY(), player
							.getPlane()), time * 600);
				}
				player.getPackets().sendObjectAnimation(new WorldObject(26198, 10, 2, player.getX() + 1, player.getY() - 2, player.getPlane()), new Animation(6363));
				player.getSkills().addXp(Skills.SMITHING, 170);
			}
		},  13);
		player.setNextAnimationNoPriority(PLAYER_ANIMATION);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextAnimationNoPriority(PLAYER_ANIMATION);
				long time = respawnDelay * 600;
				if(object.getRotation() == 0) {
				World.spawnTemporaryObject(
						new WorldObject(ShovelConstants.STAGE_2.getNextObject(), 10,
								0, player.getX() - 1, player.getY() - 1, player
								.getPlane()), time * 600);
				} else {
					World.spawnTemporaryObject(new WorldObject(ShovelConstants.STAGE_2.getNextObject(), 10, 2, player.getX() + 1, player.getY(), player
									.getPlane()), time * 600);
				}
				player.getPackets().sendObjectAnimation(new WorldObject(26198, 10, 2, player.getX() + 1, player.getY() - 2, player.getPlane()), new Animation(6363));
				player.getSkills().addXp(Skills.SMITHING, 170);
			}
		},  23);
		player.setNextAnimationNoPriority(PLAYER_ANIMATION);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				long time = respawnDelay * 600;
				if(object.getRotation() == 0) {
					if(stage.getPlotStage(PlotStage.FURNACE) == 0) {
						stage.setPlotStage(PlotStage.FURNACE, 1);
					} else {
						stage.setPlotStage(PlotStage.FURNACE, 2);
					}
					World.spawnTemporaryObject(new WorldObject(ShovelConstants.STAGE_3.getNextObject(), 10, 0, player.getX() - 1, player.getY() - 1, player
								.getPlane()), time * 600);
					/**
					 * Spawning Mould
					 */
					if(stage.getPlotStage(PlotStage.FURNACE) == 2) {
						player.getPackets().sendObjectAnimation(new WorldObject(26336, 10, 2, player.getX(), player.getY() + 2, player.getPlane()), new Animation(6361));
						spawnMould(player);
					}
				} else {
					if(stage.getPlotStage(PlotStage.FURNACE) == 0) {
						stage.setPlotStage(PlotStage.FURNACE, 1);
					} else {
						stage.setPlotStage(PlotStage.FURNACE, 2);
					}
					World.spawnTemporaryObject(new WorldObject(ShovelConstants.STAGE_3.getNextObject(), 10, 2, player.getX() + 1, player.getY(), player
							.getPlane()), time * 600);
					/**
					 * Spawning Mould
					 */
					if(stage.getPlotStage(PlotStage.FURNACE) == 2) {
						player.getPackets().sendObjectAnimation(new WorldObject(26336, 10, 2, player.getX(), player.getY() + 2, player.getPlane()), new Animation(6361));
					}
				}
				player.getSkills().addXp(Skills.SMITHING, 170);
				player.unlock();
			}
		},  46);
		return -1;
	}
	
	public void spawnMould(Player player) {
		ClanCitadel citadel = player.getClanManager().getClan().getClanCitadel();
		WorldTasksManager.schedule(new WorldTask() {
			long time = respawnDelay * 600;
			@Override
			public void run() {
				World.spawnTemporaryObject(new WorldObject(MouldConstants.STAGE_1.getCurrentObject(), 10, 2, citadel.getBoundChuncks()[0] * 0 + 97, citadel.getBoundChuncks()[1] * 0 + 140, player
						.getPlane()), time * 600);
			}
			
		}, 13);
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 2);
	}

}
