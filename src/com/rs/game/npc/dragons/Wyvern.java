package com.rs.game.npc.dragons;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.Drop;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.SlayerTask;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.NPCDrops;
import com.rs.utils.Utils;

/**
 * 
 * @author ryan
 *
 */

@SuppressWarnings("serial")
public class Wyvern extends NPC {

	public Wyvern(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(5000);
		setForceTargetDistance(64);
		setForceFollowClose(false);
		setNoDistanceCheck(true);
		setIntelligentRouteFinder(false);
		this.setForceAgressive(false);
		setCantDoDefenceEmote(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && getPossibleTargets().isEmpty()) {
			/* setCapDamage(1000); */
			heal(100000);
		}
	}

	List<Player> rewardList = new ArrayList<Player>();

	@Override
	public void handleIngoingHit(Hit hit) {
		super.handleIngoingHit(hit);
		if (hit.getSource() instanceof Player) {
			Player p = (Player) hit.getSource();
			if (p.npcWyvern == null) {// this sets the damage and boss
				p.npcWyvern = this;
				p.npcWyvernDmg = hit.getDamage();
			}
			if (p.npcWyvern != this) {// this resets the damage
				p.npcWyvern = this;
				p.npcWyvernDmg = hit.getDamage();
			}
			if (p.npcWyvern == this && p.npcWyvernDmg < 1) {// this adds to the damage
				p.npcWyvernDmg += hit.getDamage();
			}
			if (!rewardList.contains(p) && p.npcWyvernDmg >= 1) {
				rewardList.add(p);
				//p.sm("[" + this.getName() + "] " + Colors.GREEN + "You dealt enough damage to recieve reward!");
			}
		}
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 2.00;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 2.50;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 2.50;
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		if (source instanceof Player) {
			boolean reset = true;
			for (int i : noResetCombat) {
				if (i == id) {
					reset = false;
					break;
				}
			}
			if (reset)
				((Player) source).deathResetCombat();
		}
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					setNextAnimation(new Animation(defs.getDeathEmote()));
				else if (loop >= defs.getDeathDelay()) {
					customDrop();// CUSTOM DROP SHARE WILL BE HERE
					reset();
					finish();
					startSpawn();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	protected void startSpawn() {
		int respawnDelay = getCombatDefinitions().getRespawnDelay();
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				try {
					new Wyvern(id, getRespawnTile(), getMapAreaNameHash(), canBeAttackFromOutOfArea(), isSpawned());
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, respawnDelay);

	}

	protected void customDrop() {
		if (!rewardList.isEmpty()) {
			for (Player p : rewardList) {
				if (World.isOnline(p.getUsername()) && p.withinDistance(this, 20)) {// checks if player is online and
																					// within 20 tile distance of the
																					// boss
					processOriginalDrops(p);
				}
			}
		}
		rewardList.clear();
	}

	private void processOriginalDrops(Player killer) {
		try {
			increaseKillStatistics(killer, getName());
			handlePetDrop(killer, getName());
			Drop[] drops = NPCDrops.getDrops(id);
			Drop[] possibleDrops = new Drop[drops.length];
			int possibleDropsCount = 0;
			for (Drop drop : drops) {
				if (killer.getTreasureTrails().isScroll(drop.getItemId())) {
					if (killer.getTreasureTrails().hasClueScrollItem())
						continue;
				}
				if (drop.getRate() == 100)
					sendDrop(killer, drop);
				else {
					double rate = drop.getRate();
					double random = Utils.getRandomDouble(100);
					rate += killer.getDropRate();
					if (rate > 100)
						continue;
					if (random <= rate)
						possibleDrops[possibleDropsCount++] = drop;
				}
			}
			if (possibleDropsCount > 0) {
				sendDrop(killer, possibleDrops[Utils.getRandom(possibleDropsCount - 1)]);
			}
			SlayerTask.onKill(killer, this);
			ContractHandler.checkContract(killer, id, this);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}

	}

	// test
	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playerIndexes != null) {
				for (int npcIndex : playerIndexes) {
					Player player = World.getPlayers().get(npcIndex);
					if (player == null || player.isDead() || player.hasFinished() || !player.isRunning()
							|| !player.withinDistance(this, 64)
							|| ((!isAtMultiArea() || !player.isAtMultiArea()) && player.getAttackedBy() != this
									&& player.getAttackedByDelay() > System.currentTimeMillis())
							|| !clipedProjectile(player, false))
						continue;
					possibleTarget.add(player);
				}
			}
		}
		return possibleTarget;
	}
	// test

}
