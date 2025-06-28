package com.rs.game.npc.araxxor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.discord.Discord;
import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.OwnedObjectManager;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.controllers.AraxyteHyveController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/*import mysql.impl.NewsManager;*/

/**
 * Constructs the Araxxor NPC.
 * 
 * @author Zeus
 */
public class AraxyteNPC extends NPC {

	/**
	 * Generated UID.
	 */
	private static final long serialVersionUID = 8847379709307749767L;

	/**
	 * Represents the spiderling data.
	 */
	private byte spawnedSpiders;
	private NPC[] spiders;

	/**
	 * Construct the NPC here.
	 * 
	 * @param id   The NPC ID.
	 * @param tile The WorldTile.
	 */
	public AraxyteNPC(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		/*
		 * if (!Settings.DEBUG) setCapDamage(Utils.random(1511, 1750));
		 */
		setForceAgressive(true);
		setForceTargetDistance(12);
		setRun(true);
		setIntelligentRouteFinder(true);
		heal1 = false;
		heal2 = false;
		phase2 = false;
		spiders = new NPC[3];
	}

	@Override
	public void processNPC() {
		if (isDead() || isCantInteract())
			return;
		if (!getCombat().process())
			checkAgressivity();
		if (getHitpoints() <= 7000 && !AraxyteNPC.phase2 && AraxyteNPC.heal2) {
			setCantInteract(true);
			setNextAnimation(new Animation(24056));
			setTarget(null);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (step <= 1)
						spawnSpider();
					if (step == 2)
						setNextWorldTile(new WorldTile(4536, 6312, 1));
					if (step == 10) {
						for (Player player : AraxyteHyveController.getPlayers()) {
							player.sendMessage(
									Colors.darkRed + "Araxxor prepares to come down from the ceiling and charge!");
						}
					}
					if (step == 15) {
						setHitpoints(getMaxHitpoints());
						setNextWorldTile(new WorldTile(4503, 6262, 1));
						setNextAnimation(new Animation(24076));
						AraxyteNPC.heal2 = false;
					}
					if (step == 18) {
						AraxyteNPC.phase2 = true;
						start();
						stop();
					}
					step++;
				}

				int step;
			}, 0, 1);
		}
		if (getHitpoints() <= 10000 && !AraxyteNPC.heal2) { // second heal
			setCantInteract(true);
			setNextGraphics(new Graphics(4987));
			setNextAnimation(new Animation(24075));
			setTarget(null);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (step == 0) {
						applyHit(new Hit(null, 1875, HitLook.HEALED_DAMAGE));
					}
					if (step == 2) {
						applyHit(new Hit(null, 2062, HitLook.HEALED_DAMAGE));
					}
					if (step == 4) {
						applyHit(new Hit(null, 2250, HitLook.HEALED_DAMAGE));
					}
					if (step == 6) {
						applyHit(new Hit(null, 2437, HitLook.HEALED_DAMAGE));
					}
					if (step == 8) {
						AraxyteNPC.heal2 = true;
						start();
						stop();
					}
					step++;
				}

				int step;
			}, 0, 1);
		}
		if (getHitpoints() <= 25000 && !heal1) { // first heal
			setCantInteract(true);
			setNextGraphics(new Graphics(4987));
			setNextAnimation(new Animation(24075));
			setTarget(null);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (step == 0) {
						applyHit(new Hit(null, 937, HitLook.HEALED_DAMAGE));
					}
					if (step == 2) {
						applyHit(new Hit(null, 1125, HitLook.HEALED_DAMAGE));
					}
					if (step == 4) {
						applyHit(new Hit(null, 1312, HitLook.HEALED_DAMAGE));
					}
					if (step == 6) {
						applyHit(new Hit(null, 1500, HitLook.HEALED_DAMAGE));
					}
					if (step == 8) {
						AraxyteNPC.heal1 = true;
						start();
						stop();
					}
					step++;
				}

				int step;
			}, 0, 1);
		}
		super.processNPC();
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit.getDamage() >= getHitpoints() && !(AraxyteNPC.phase2 || AraxyteNPC.heal2))
			hit.setDamage(getHitpoints() - 1);
		super.handleIngoingHit(hit);
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		setCantInteract(true);
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		removeSpider();
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				if (loop == 0)
					setNextAnimation(new Animation(defs.getDeathEmote()));
				if (loop >= defs.getDeathDelay() + 1) {
					setNextNPCTransformation(19463);
					Player killer = getMostDamageReceivedSourcePlayer();
					sendObject(killer);
					drop();
					reset();
					finish();
					AraxyteHyveController.endFight();
					stop();
				}
				loop++;
			}

			int loop;
		}, 0, 1);
	}

	/**
	 * Starts the Araxxor NPC.
	 */
	public void start() {
		setCantInteract(false);
		Entity target = AraxyteHyveController.getRandomTarget();
		if (target != null)
			setTarget(target);
	}

	/**
	 * Spawns the Spiderlings.
	 */
	public void spawnSpider() {
		if (spawnedSpiders >= spiders.length)
			return;
		for (int tryI = 0; tryI < 10; tryI++) {
			WorldTile tile = new WorldTile(getMiddleWorldTile(), 2);
			if (World.isTileFree(0, tile.getX(), tile.getY(), 1)) {
				NPC spider = spiders[spawnedSpiders++] = new NPC(19460, tile, -1, true, true);
				spider.setNextAnimation(new Animation(24135));
				spider.setForceAgressive(true);
				spider.setForceTargetDistance(24);
				spider.setForceFollowClose(true);
				spider.setIntelligentRouteFinder(true);
				break;
			}
		}
	}

	/**
	 * Kills all spawned Spiderlings.
	 */
	public void removeSpider() {
		for (NPC minion : spiders) {
			if (minion == null)
				continue;
			minion.sendDeath(this);
		}
	}

	/**
	 * Sends Araxxi's body object as handler.
	 * 
	 * @param Player the player who killed Araxxor.
	 */
	private void sendObject(Player player) {
		if (player == null)
			return;
		prepareRewards();
		WorldObject object = new WorldObject(91673, 10, 0, getLastWorldTile());
		ContractHandler.checkContract(player, id, this);
		World.spawnObjectTemporary(player, object, 30000);

		OwnedObjectManager.addOwnedObjectManager(player,
				new WorldObject[] { new WorldObject(91673, 10, 0, getLastWorldTile()) }, new long[] { 30000 });

		player.sendMessage("You've killed a total of " + Colors.red + player.increaseKillStatistics("araxxor", true)
				+ "</col> x " + Colors.red + "Araxxor</col>.", true);
		for (Player all : AraxyteHyveController.getPlayers()) {
			if (all == null || all == player)
				continue;
			all.sendMessage(Colors.darkRed + player.getDisplayName() + " has slain the Araxxor; "
					+ "his total Araxxor kills: " + player.increaseKillStatistics("araxxor", false) + ".");
		}
		if (rewards != null) {
			if (rewards.contains(new Item(31722)))
				sendDropNews(player, new Item(31722));
			if (rewards.contains(new Item(31723)))
				sendDropNews(player, new Item(31723));
			if (rewards.contains(new Item(31724)))
				sendDropNews(player, new Item(31724));
			if (rewards.contains(new Item(31718))) {
				for (Player all : AraxyteHyveController.getPlayers()) {
					if (all == null || all == player)
						continue;
					all.sendMessage(Colors.orange + "<shad=000000><img=6>Araxyte cave: " + player.getDisplayName()
							+ " received a Spider leg top.");
				}
			}
			if (rewards.contains(new Item(31719))) {
				for (Player all : AraxyteHyveController.getPlayers()) {
					if (all == null || all == player)
						continue;
					all.sendMessage(Colors.orange + "<shad=000000><img=6>Araxyte cave: " + player.getDisplayName()
							+ " received a Spider leg middle.");
				}
			}
			if (rewards.contains(new Item(31720))) {
				for (Player all : AraxyteHyveController.getPlayers()) {
					if (all == null || all == player)
						continue;
					all.sendMessage(Colors.orange + "<shad=000000><img=6>Araxyte cave: " + player.getDisplayName()
							+ " received a Spider leg bottom.");
				}
			}
		}
	}

	/**
	 * Sends the Rare drop news.
	 * 
	 * @param player The player receiving the drop.
	 * @param item   The Item being dropped.
	 */
	private void sendDropNews(Player player, Item item) {
		Discord.sendAchievement("News: " + player.getDisplayName() + " received " + Utils.getAorAn(item.getName()) + " "
				+ item.getName() + " from " + getName() + ".");
		World.sendWorldMessage(Colors.orange + "<shad=000000><img=6>News: " + player.getDisplayName() + " received "
				+ Utils.getAorAn(item.getName()) + " " + item.getName() + " from " + getName() + ".", false);

		/*
		 * new Thread(new NewsManager(player,
		 * "<b><img src=\"./bin/images/news/drop.png\" height=14> " +
		 * player.getDisplayName() + " received "+Utils.getAorAn(item.getName()) + " " +
		 * item.getName()+" from " + getName() + ".")).start();
		 */
	}

	/**
	 * Booleans representing if Araxxor has already used its abilities.
	 */
	public static boolean heal1, heal2, phase2;

	/**
	 * The rewards container.
	 */
	private final static ItemsContainer<Item> rewards = new ItemsContainer<>(10, true);

	/**
	 * Opens the reward chest.
	 * 
	 * @param replace If the chest should be replaced with an opened one.
	 */
	public static void openRewards(Player player) {
		player.getInterfaceManager().sendInterface(1284);
		player.getPackets().sendInterSetItemsOptionsScript(1284, 7, 100, 8, 3, "Take", "Bank", "Discard", "Examine");
		player.getPackets().sendUnlockIComponentOptionSlots(1284, 7, 0, 10, 0, 1, 2, 3);
		player.getPackets().sendItems(100, rewards);
	}

	/**
	 * Prepares the rewards.
	 */
	public static void prepareRewards() {
		List<Item> rewardTable = new ArrayList<Item>();
		for (int[] reward : REWARDS) {
			Item item = new Item(reward[0], reward[1] + Utils.random(reward[2] - reward[1]));
			for (int i = 0; i < reward[3]; i++)
				rewardTable.add(item);
		}
		Collections.shuffle(rewardTable);
		for (int i = 0; i < 3 + Utils.random(2); i++)
			rewards.add(rewardTable.get(Utils.random(rewardTable.size())));
		if (!rewards.contains(new Item(31737)))
			rewards.add(new Item(31737, Utils.random(70, 90)));
	}

	/**
	 * The rewards.
	 */
	private final static int[][] REWARDS = { { 18778, 1, 1, 7 }, // Effigy
			{ 15272, 8, 11, 75 }, // Rocktail
			{ 23351, 3, 15, 76 }, // Sara brew flask (6)
			{ 23399, 2, 5, 77 }, // Super restore flask (6)
			{ 23531, 2, 6, 11 }, // Overload flask (6)
			{ 2485, 40, 55, 78 }, // Lantadyme
			{ 217, 45, 45, 79 }, // Dwarf weed
			{ 211, 45, 45, 86 }, // Avantoe
			{ 1513, 150, 323, 84 }, // Magic logs
			{ 1515, 600, 600, 83 }, // Yew logs
			{ 449, 100, 100, 81 }, // Adamantite ore
			{ 453, 600, 600, 82 }, // Coal
			{ 995, 400024, 449225, 45 }, // Coins
			{ 1747, 70, 90, 43 }, // Black dragonhide
			{ 29863, 2, 3, 11 }, // Sirenic scale
			{ 1127, 10, 10, 51 }, // Rune platebody
			{ 1444, 10, 70, 37 }, // Water talisman
			{ 451, 50, 50, 38 }, // Runite ore
			{ 5303, 10, 10, 39 }, // Dwarf weed seed
			{ 5316, 2, 8, 44 }, // Magic seed
			{ 1391, 32, 70, 43 }, // Battlestaff
			{ 9245, 40, 250, 38 }, // Onyx bolts (e)
			{ 31867, 40, 40, 20 }, // Hydrix bolt tips
			{ 29863, 2, 3, 18 }, // Sirenic scale
			{ 6571, 2, 2, 17 }, // Uncut onyx
			{ 31718, 1, 1, 15 }, // Spider leg top
			{ 31719, 1, 1, 15 }, // Spider leg middle
			{ 31720, 1, 1, 15 }, // Spider leg bottom
			{ 31722, 1, 1, 4 }, // Araxxi's fang
			{ 31723, 1, 1, 4 }, // Araxxi's eye
			{ 31724, 1, 1, 4 }, // Araxxi's web
	};

	/**
	 * Gets the rewards.
	 * 
	 * @return The rewards.
	 */
	public static ItemsContainer<Item> getRewards() {
		return rewards;
	}
}