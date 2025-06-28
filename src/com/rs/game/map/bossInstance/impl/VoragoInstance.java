package com.rs.game.map.bossInstance.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.Projectile;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.map.bossInstance.BossInstance;
import com.rs.game.map.bossInstance.InstanceSettings;
import com.rs.game.npc.vorago.Vorago;
import com.rs.game.npc.vorago.VoragoFace;
import com.rs.game.player.Player;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class VoragoInstance extends BossInstance {

	public static WorldTile PHASE_1 = new WorldTile(3104, 6112, 0), PHASE_2 = new WorldTile(3040, 6048, 0), PHASE_3 = new WorldTile(3104, 6048, 0), PHASE_4 = new WorldTile(3040, 5984, 0), PHASE_5 = new WorldTile(3104, 5984, 0);

	private transient List<Player> acceptedChallenge;
	private transient List<Player> playersOnBattle;
	private transient VoragoFace voragoFace;
	private transient Vorago vorago;
	private transient WorldObject jumpGap;
	private transient WorldObject exitSphere;
	public boolean startAttack;
	public boolean[] arttributes = new boolean[1];// 0 - Jumping Started
	public int START_HIT_DAMAGE = getSettings().isHardMode() ? 700 : 500;

	/**
	 * TODO Add Zero To START HIT
	 */

	public VoragoInstance(Player owner, InstanceSettings settings) {
		super(owner, settings);
		acceptedChallenge = new CopyOnWriteArrayList<Player>();
		playersOnBattle = new CopyOnWriteArrayList<Player>();
	}

	public void sendChallenge(Player challenger) {
		if (startAttack)
			return;
		if (!acceptedChallenge.isEmpty())
			return;
		startAttack = true;
		sendStartAttack();
		acceptedChallenge.add(challenger);
		challenger.getPackets().sendGameMessage("<col=EE7600>Vorago accepted your challenge and begins to charge a massive attack.");
		for (Player reciever : getPlayers()) {
			if (reciever == null || reciever == challenger || !reciever.hasSpokenToVorago() || !isInChallengeArea(reciever))
				continue;
			reciever.lock();
			reciever.stopAll();
			reciever.getDialogueManager().startDialogue(new Dialogue() {
				@Override
				public void start() {
					this.sendOptionsDialogue(challenger.getDisplayName() + " HAS CHALLENGED VORAGO<br>ARE YOU WILLING TO FACE THE TEST WITH THEM?", "Yes", "No");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					if (getVoragoFace().isBattleInGoing()) {
						player.getPackets().sendGameMessage("Looks like vorago is not here.");
						end();
						return;
					}
					if (componentId == OPTION_1) {
						acceptedChallenge.add(player);
						reciever.getPackets().sendGameMessage("<col=EE7600>Vorago accepted your challenge and begins to charge a massive attack.");
					}
					player.unlock();
					end();
				}

				@Override
				public void finish() {
				}
			});
		}
	}

	private void sendStartAttack() {
		if (getVoragoFace().isBattleInGoing())
			return;
		WorldTasksManager.schedule(new WorldTask() {
			int count = 0;

			@Override
			public void run() {
				if (!isPublic() && !getSettings().hasTimeRemaining()) {
					stop();
					startAttack = false;
					return;
				}
				if (count == 16) {
					World.sendGraphics(voragoFace, new Graphics(4033, 1, 1, 1), getTile(new WorldTile(3040, 6122, 0)));
				} else if (count == 17) {
					if (acceptedChallenge.size() > 0) {
						int damage = (START_HIT_DAMAGE / acceptedChallenge.size());
						for (Player player : acceptedChallenge) {
							if (player == null)
								continue;
							player.faceEntity(voragoFace);
							if (damage >= player.getHitpoints()) {
								player.sendDeath(voragoFace);
								if (acceptedChallenge.contains(player)) {
									removePlayer(player, BossInstance.DIED);
								}
							} else {
								WorldTile toTile = getTile(new WorldTile(3030 + Utils.random(8), 6118 - Utils.random(3), 0));
								player.setNextForceMovement(new ForceMovement(toTile, 1, ForceMovement.NORTH));
								player.setNextAnimation(new Animation(20338));
								player.setNextGraphics(new Graphics(4034));
								player.applyHit(new Hit(voragoFace, damage, HitLook.REGULAR_DAMAGE));
							}
						}
					}
				} else if (count == 18) {
					if (startAttack) {
						if (acceptedChallenge.size() > 0)
							enterBattle();
						startAttack = false;
					}
					stop();
				}
				count++;
			}
		}, 0, 1);
	}

	public void enterBattle() {
		voragoFace.setBattleInGoing(true);
		VoragoInstance instance = this;
		WorldTasksManager.schedule(new WorldTask() {
			int count = 0;

			@Override
			public void run() {
				switch (count) {
				case 0:
					for (Player player : acceptedChallenge) {
						if (player == null || player.isDead())
							continue;
						acceptedChallenge.remove(player);
						playersOnBattle.add(player);
						player.setForceMultiArea(true);
						player.setNextWorldTile(randomSpawnTile(player, getVoragoSpawnLocation(1), 5));
						player.setNextAnimation(new Animation(20401));
					}
					break;
				case 1:
					for (Player player : playersOnBattle) {
						if (player == null || player.isDead())
							continue;
						if (getSettings().isHardMode())
							welcomePlayerToHardMode(player);
					}
					break;
				case 9:
					if (playersOnBattle.isEmpty()) {
						voragoFace.setBattleInGoing(false);
						stop();
						break;
					}
					vorago = new Vorago(17182, getVoragoSpawnLocation(1), -1, true, instance);
					vorago.setNextAnimation(new Animation(20367));
					vorago.checkAgressivity();
					break;
				case 10:
					vorago.giveFirstWeaponPiece();
					stop();
					break;
				}
				count++;
			}
		}, 0, 1);
	}

	public void welcomePlayerToHardMode(Player player) {
		player.getPackets().sendPlayerMessage(1, 15263739, "Welcome to hard mode!", true);
		long startTime = Utils.currentTimeMillis();
		long arriveTime = startTime + (20000);// arrives after 4 seconds
		double speed = Utils.getProjectileSpeed(new WorldTile(player.getX() - 3, player.getY(), player.getPlane()), player, 250, 20, startTime, arriveTime);
		Projectile projectile = World.sendProjectileNew(new WorldTile(player.getX() - 3, player.getY(), player.getPlane()), player, 4023, 250, 20, 10, speed, 1, 1);
		int cycleTime = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					for (Player p : getPlayersOnBattle()) {
						if (p == null || p.isDead() || Utils.getDistance(p, player) > 2)
							continue;
						int damage = 200 + (getPlayersNearby(player, 3) * 100);
						if (damage > 700)
							damage = 700;
						p.setNextGraphics(new Graphics(4024));
						World.sendGraphics(null, new Graphics(3522), new WorldTile(p.getX(), p.getY(), p.getPlane()));
						p.applyHit(new Hit(player, damage, HitLook.REGULAR_DAMAGE));
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, (cycleTime * 1000) - 950);
	}

	public int getPlayersNearby(Player target, int withinDistance) {
		int count = 0;
		for (Player player : getPlayersOnBattle()) {
			if (player == null || player.isDead() || Utils.getDistance(target, player) > withinDistance || player == target)
				continue;
			count++;
		}
		return count > 5 ? 5 : count;
	}

	public WorldTile randomSpawnTile(Entity target, WorldTile checkTile, int randomizeSize) {
		WorldTile teleTile = null;
		// attemps to randomize tile by 4x4 area
		int size = target == null ? 1 : target.getSize();
		for (int trycount = 0; trycount < 10; trycount++) {
			teleTile = new WorldTile(checkTile, randomizeSize);
			if (!ignoreTile(teleTile) || World.canMoveNPC(checkTile.getPlane(), teleTile.getX(), teleTile.getY(), size))
				break;
			teleTile = checkTile;
		}
		return teleTile;
	}

	public boolean ignoreTile(WorldTile tile) {
		if (vorago != null) {
			int phase = vorago.getPhase();
			boolean hardMode = getSettings().isHardMode();
			WorldTile voragoL = getVoragoSpawnLocation(phase);
			WorldTile center = new WorldTile(voragoL.getX() + 2, voragoL.getY() + 2, voragoL.getPlane());
			boolean insidePhaseArea = ((!hardMode && phase != 5) || (hardMode && phase < 10)) ? (tile.getX() >= (center.getX() - 12) && tile.getX() <= (center.getX() + 11) && tile.getY() >= (center.getY() - 12) && tile.getY() <= (center.getY() + 11)) : ((tile.getX() >= (center.getX() - 13) && tile.getX() <= (center.getX() + 10) && tile.getY() >= (center.getY() - 2) && tile.getY() <= (center.getY() + 2)));
			return !insidePhaseArea;
		}
		return false;
	}

	public WorldTile getSimilarCoords(WorldTile entity, int nextphase) {
		boolean hardMode = getSettings().isHardMode();
		if (hardMode) {
			switch (nextphase) {
			case 2:
				return new WorldTile(entity.getX() - 64, entity.getY() - 64, 0);
			case 3:
				return new WorldTile(entity.getX() + 64, entity.getY(), 0);
			case 4:
				return new WorldTile(entity.getX() - 64, entity.getY(), 0);
			case 5:
				return new WorldTile(entity.getX() + 64, entity.getY(), 0);
			case 6:
				return new WorldTile(entity.getX() - 64, entity.getY(), 0);
			case 7:
				return new WorldTile(entity.getX() + 64, entity.getY(), 0);
			case 8:
				return new WorldTile(entity.getX() - 64, entity.getY(), 0);
			case 9:
				return new WorldTile(entity.getX(), entity.getY() - 64, 0);
			}
		} else {
			switch (nextphase) {
			case 2:
				return new WorldTile(entity.getX() - 64, entity.getY() - 64, 0);
			case 3:
				return new WorldTile(entity.getX() + 64, entity.getY(), 0);
			case 4:
				return new WorldTile(entity.getX() - 64, entity.getY() - 64, 0);
			}
		}
		if (nextphase == 11) {
			if (entity.getX() < getVoragoSpawnLocation(11).getX()) {
				WorldTile checkTile = new WorldTile(3111, 5985, 0);
				WorldTile teleTile = null;
				for (int trycount = 0; trycount < 10; trycount++) {
					teleTile = new WorldTile(checkTile, 2);
					if (World.isTileFree(checkTile.getPlane(), teleTile.getX(), teleTile.getY(), 1))
						break;
					teleTile = checkTile;
				}
				return getTile(teleTile);
			} else
				return null;
		}
		WorldTile checkTile = new WorldTile(3095, 5985, 0);
		WorldTile teleTile = null;
		for (int trycount = 0; trycount < 10; trycount++) {
			teleTile = new WorldTile(checkTile, 2);
			if (World.isTileFree(checkTile.getPlane(), teleTile.getX(), teleTile.getY(), 1))
				break;
			teleTile = checkTile;
		}
		return getTile(teleTile);
	}

	public WorldTile getVoragoSpawnLocation(int phase) {
		boolean hardMode = getSettings().isHardMode();
		if (hardMode) {
			switch (phase) {
			case 1:
				return getTile(new WorldTile(3102, 6110, 0));
			case 2:
				return getTile(new WorldTile(3038, 6046, 0));
			case 3:
				return getTile(new WorldTile(3102, 6046, 0));
			case 4:
				return getTile(new WorldTile(3038, 6046, 0));
			case 5:
				return getTile(new WorldTile(3102, 6046, 0));
			case 6:
				return getTile(new WorldTile(3038, 6046, 0));
			case 7:
				return getTile(new WorldTile(3102, 6046, 0));
			case 8:
				return getTile(new WorldTile(3038, 6046, 0));
			case 9:
				return getTile(new WorldTile(3038, 5982, 0));
			default:
				return getTile(new WorldTile(3102, 5982, 0));
			}
		} else {
			switch (phase) {
			case 1:
				return getTile(new WorldTile(3102, 6110, 0));
			case 2:
				return getTile(new WorldTile(3038, 6046, 0));
			case 3:
				return getTile(new WorldTile(3102, 6046, 0));
			case 4:
				return getTile(new WorldTile(3038, 5982, 0));
			case 5:
			default:
				return getTile(new WorldTile(3102, 5982, 0));
			}
		}
	}

	@Override
	public int[] getMapSize() {
		return new int[] { 2, 3 };
	}

	@Override
	public int[] getMapPos() {
		return new int[] { 376, 744 };
	}

	@Override
	public void loadMapInstance() {
		voragoFace = new VoragoFace(17162, getTile(new WorldTile(3038, 6128, 0)), -1, false, this);
		jumpGap = new WorldObject(84825, 10, 0, getTile(new WorldTile(3099, 6123, 0)));
		World.spawnObject(jumpGap);
	}

	public Vorago getVorago() {
		return vorago;
	}

	public VoragoFace getVoragoFace() {
		return voragoFace;
	}

	public List<Player> getAcceptedChallenge() {
		return acceptedChallenge;
	}

	public List<Player> getPlayersOnBattle() {
		return playersOnBattle;
	}

	public boolean ChallengeStarted() {
		return acceptedChallenge.size() != 0;
	}

	public boolean isInChallengeArea(Player player) {
		return (player.getX() >= (voragoFace.getX() - 19) && player.getX() <= (voragoFace.getX() + 19) && player.getY() >= (voragoFace.getY() - 14) && player.getY() <= (voragoFace.getY() + 9));
	}

	/*
	 * public boolean isInBoreHoleArea(Player player) { return (player.getX() >=
	 * 3012 && player.getX() <= 3062 && player.getY() >= 6087 && player.getY()
	 * <= 6133); }
	 */

	public void removePlayer(Player player, int type) {
		if (acceptedChallenge.contains(player))
			acceptedChallenge.remove(player);
		if (type != BossInstance.LOGGED_OUT && type != BossInstance.TELEPORTED)
			this.startAttack = false;
		if (isPublic()) {
			if (type == BossInstance.LOGGED_OUT) {
				getPlayers().remove(player);
			} else if (type != BossInstance.DIED)
				leaveInstance(player, type);
		} else {
			player.getControlerManager().removeControlerWithoutCheck();
			if (type != BossInstance.DIED)
				leaveInstance(player, type);
		}

	}

	public void removePlayerFromBattle(Player player, int type) {
		playersOnBattle.remove(player);
		if (isPublic()) {
			if (type == BossInstance.LOGGED_OUT) {
				player.setLocation(randomSpawnTile(player, getTile(new WorldTile(3047, 6121, 0)), 2));
			} else if (type != BossInstance.DIED) {
				player.setNextWorldTile(randomSpawnTile(player, getTile(new WorldTile(3047, 6121, 0)), 2));
			}
		} else {
			if (type == BossInstance.LOGGED_OUT) {
				player.getControlerManager().removeControlerWithoutCheck();
				leaveInstance(player, type);
			} else if (type != BossInstance.DIED) {
				player.setNextWorldTile(randomSpawnTile(player, getTile(new WorldTile(3047, 6121, 0)), 2));
			}
		}
		checkEndBattle();
	}

	public boolean checkEndBattle() {
		if (playersOnBattle.isEmpty()) {
			if (vorago != null) {
				vorago.setFinished(true);
				vorago = null;
			}
			if (exitSphere != null) {
				World.removeObject(exitSphere);
				exitSphere = null;
			}
			voragoFace.setBattleInGoing(false);
			startAttack = false;
			return true;
		}
		return false;
	}

	public void finishPhase(Player player) {
		boolean hardMode = getSettings().isHardMode();
		vorago.setPhaseProgress(vorago.getPhase() == 1 ? 0 : vorago.getPhase() == 2 ? 5 : ((!hardMode && vorago.getPhase() == 4) || (hardMode && vorago.getPhase() == 9)) ? 3 : 0);
		vorago.sentDeath = false;
		vorago.sendDeath(player);
	}

	public boolean playerIsOnBattle(Player player) {
		if (voragoFace == null || playersOnBattle == null)
			return false;
		return voragoFace.isBattleInGoing() && playersOnBattle.contains(player);
	}

	@Override
	public void finish() {
		if (!isPublic()) {
			World.removeNPC(voragoFace);
			World.removeObject(jumpGap);
		}
		super.finish();
	}

	public void sendMessage(Player player, int border, String message) {
		if (getSettings().isHardMode())
			return;
		player.getPackets().sendPlayerMessage(border, 15263739, message, true);
	}

	public void sendMessage(Player player, int border, String message, boolean sendGameMessage) {
		if (getSettings().isHardMode())
			return;
		player.getPackets().sendPlayerMessage(border, 15263739, message, sendGameMessage);
	}

	public void spawnExitSphere() {
		boolean hardMode = getSettings().isHardMode();
		WorldTile sphereLocation = getTile(new WorldTile(3105, 5985, 0));
		WorldTile voragoLocation = hardMode ? getTile(new WorldTile(3092, 5984, 0)) : getTile(new WorldTile(3112, 5984, 0));
		Projectile projectile = World.sendProjectileNew(voragoLocation, sphereLocation, 4029, 0, 30, 10, 0.8, 30, 1);
		int cycleTime = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
		exitSphere = new WorldObject(84960, 10, 0, sphereLocation);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				World.spawnObject(exitSphere);
			}
		}, cycleTime);
	}

	public void handleExitSphereClick(Player player) {
		player.lock();
		Projectile projectile = World.sendProjectileNew(getTile(new WorldTile(3105, 5985, 0)), player, 4029, 0, 20, 10, 0.5, 30, 1);
		int cycleTime = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				removePlayerFromBattle(player, BossInstance.EXITED);
				player.unlock();
			}
		}, cycleTime);
	}

	public static void checkChangeRotation() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate startDate = LocalDate.parse(Settings.VORAGO_RELEASE_DATE, formatter);
		LocalDate currentDate = LocalDate.now();
		long daysPassed = ChronoUnit.DAYS.between(startDate, currentDate);
		int rotation = 0;
		for (int i = 0; i < daysPassed / Settings.DAYS_TO_CHANGE_ROTATION; i++) {
			rotation = rotation + 1 >= Settings.VORAGO_ROTATION_NAMES.length ? 0 : rotation + 1;
		}
		Settings.VORAGO_ROTATION = rotation;
	}
}
