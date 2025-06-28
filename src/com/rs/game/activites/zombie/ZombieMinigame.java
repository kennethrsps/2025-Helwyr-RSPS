package com.rs.game.activites.zombie;

import java.util.List;
import java.util.TimerTask;

import com.rs.cores.CoresManager;
import com.rs.game.MapInstance;
import com.rs.game.WorldTile;
import com.rs.game.npc.others.Zombie;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.zombie.ZombieControler;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class ZombieMinigame {

	public static int ZOMBIE_ID = 15309;
	private MapInstance map;
	private List<Player> players;
	private final int[][] WAVES = { { 2, 15 }, { 3, 20 }, { 5, 20 }, { 8, 35 }, { 9, 40 }, { 10, 50 }, { 15, 70 },
			{ 20, 100 }, { 25, 105 }, { 30, 110 }, { 35, 120 } };
	private int wave = 1;
	private int aliveNPCSCount;
	public long waitTime;
	private boolean forceSkip = false;

	public ZombieMinigame(List<Player> players) {
		this.players = players;
	}

	public ZombieMinigame create() {
		final ZombieMinigame instance = this;
		int[] pos = getMapPos();
		int[] size = getMapSize();
		map = new MapInstance(pos[0], pos[1], size[0], size[1]);
		map.load(new Runnable() {
			@Override
			public void run() {
				for (Player player : players) {
					player.getControlerManager().removeControlerWithoutCheck();
					player.useStairs(-1, getRandomSpawnTile(), 1, 2);
					player.getControlerManager().startControler("ZombieControler", instance);
				}
				setWaveEvent();
			}
		});
		return instance;
	}

	public void setWaveEvent() {
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			player.getPackets().sendIComponentText(730, 26, "Time left: Waiting.");
			player.getPackets().sendIComponentText(730, 17, "Current Wave: Waiting.");
		}
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					startWave();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 6000);
	}

	public void removeNPC() {
		aliveNPCSCount--;
		if (aliveNPCSCount == 0) {
			for (Player player : getPlayers()) {
				if (player == null)
					continue;
				player.getPackets().sendGameMessage("Finished wave : " + (getCurrentWave()) + ".");
				forceSkip = true;
			}
		}
	}

	public void startWave() {
		aliveNPCSCount = WAVES[getCurrentWave() - 1][0] * getPlayers().size();
		waitTime = WAVES[getCurrentWave() - 1][1] * 1000;
		forceSkip = false;
		for (int i = 0; i < aliveNPCSCount; i++) {
			new Zombie(this, ZOMBIE_ID, getRandomSpawnTile(), -1, true);
		}
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			player.getPackets().sendGameMessage("Starting wave : " + getCurrentWave() + ". You have "
					+ (waitTime / 1000) + " seconds to kill " + aliveNPCSCount + " zombies goodluck!");
			player.getPackets().sendIComponentText(730, 17, "Current Wave: " + getCurrentWave() + ".");
		}
		WorldTasksManager.schedule(new WorldTask() {
			int timer = (int) (waitTime / 1000);

			@Override
			public void run() {
				if (timer > 0) {
					if (forceSkip) {
						processEndTime();
						stop();
					} else {
						for (Player player : getPlayers()) {
							if (player == null)
								continue;
							player.getPackets().sendIComponentText(730, 26, "Time left: " + timer + " seconds.");
						}
					}
				} else if (timer <= 0) {
					processEndTime();
					stop();
				}
				timer--;
			}
		}, 0, 1);
	}

	public void processEndTime() {
		if (aliveNPCSCount == 0) {
			if (getCurrentWave() == WAVES.length) {
				endGame(true);
			} else {
				nextWave();
			}
		} else {
			endGame(false);
		}
	}

	public void nextWave() {
		int nextWave = getCurrentWave() + 1;
		setCurrentWave(nextWave);
		setWaveEvent();
	}

	public void endGame(boolean win) {
		for (int i = 0; i < getPlayers().size(); i++) {
			Player p = getPlayers().get(i);
			if (p == null)
				continue;
			ZombieControler controler = (ZombieControler) p.getControlerManager().getControler();
			if (controler == null)
				continue;
			if (win) {
				int amount = controler.getZombiesKillCount() * 10;
				p.getPackets().sendGameMessage(
						"You have managed to defeate all the waves with your team mates congratulations.");
				p.setZombiesMinigamePoints(p.getZombiesMinigamePoints() + amount);
				p.getPackets().sendGameMessage("You have recieved " + (controler.getZombiesKillCount() * 10)
						+ " zombie minigame points. For killing " + controler.getZombiesKillCount() + " zombies.");
			} else {
				p.getPackets().sendGameMessage("You haven't killed the zombies in time therefor you lose.");
			}
			p.getControlerManager().forceStop();
		}
		getPlayers().clear();
		if (getPlayers().size() == 0)
			map.destroy(null);
	}

	public WorldTile getRandomSpawnTile() {
		int x = Utils.random(3336, 3340);
		int y = Utils.random(3212, 3216);
		return getTile(new WorldTile(x, y, 0));
	}

	public WorldTile getTile(WorldTile tile) {
		return getTile(tile.getX(), tile.getY(), tile.getPlane());
	}

	public WorldTile getTile(int x, int y, int plane) {
		int[] originalPos = map.getOriginalPos();
		WorldTile tile = map.getTile((x - originalPos[0] * 8) % (map.getRatioX() * 64),
				(y - originalPos[1] * 8) % (map.getRatioY() * 64));
		tile.moveLocation(0, 0, plane);
		return tile;
	}

	public int[] getMapSize() {
		return new int[] { 1, 1 };
	}

	public int[] getMapPos() {
		return new int[] { 416, 400 };
	}

	public int getCurrentWave() {
		return wave;
	}

	public void setCurrentWave(int wave) {
		this.wave = wave;
	}

	public void removePlayer(Player player) {
		getPlayers().remove(player);
		if (getPlayers().size() == 0)
			map.destroy(null);
	}

	public List<Player> getPlayers() {
		return players;
	}

}
