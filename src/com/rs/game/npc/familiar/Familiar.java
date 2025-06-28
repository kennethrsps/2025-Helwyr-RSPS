package com.rs.game.npc.familiar;

import java.io.Serializable;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.glacor.Glacyte;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.summoning.Summoning.Pouches;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * An abstract serialized class use to handle the Players NPC familiar.
 * 
 * @author Zeus
 */
public abstract class Familiar extends NPC implements Serializable {

	/**
	 * The generated serial UID.
	 */
	private static final long serialVersionUID = -3255206534594320406L;

	/**
	 * Special Attack types.
	 */
	public static enum SpecialAttack {
		ITEM, ENTITY, CLICK, OBJECT
	}

	/**
	 * Transient the familiar's owner.
	 */
	private transient Player owner;

	/**
	 * Define familiar's configurations.
	 */
	private int ticks, trackTimer, specialEnergy;

	/**
	 * Define's if the familiar is existing.
	 */
	private transient boolean finished;

	/**
	 * Define's if we should track the drain rate;
	 */
	private boolean trackDrain;

	/**
	 * Exclusively used for the Steel titan.
	 */
	public boolean titanAttack, titanSpec;

	/**
	 * Define's the Beast of Burden familiars.
	 */
	private BeastOfBurden bob;

	/**
	 * Defines the familiar pouch data.
	 */
	protected Pouches pouch;

	/**
	 * Define's nearest coordinates as Integers.
	 */
	private transient int[][] checkNearDirs;

	/**
	 * Defines extra familiar configurations.
	 */
	private transient boolean sentRequestMoveMessage, dead;

	/**
	 * Defines familiar's hitpoints.
	 */
	private transient int hitpoints;

	public boolean callBlocked;

	/**
	 * Constructs the actual Familiar.
	 * 
	 * @param owner
	 *            The owner.
	 * @param pouch
	 *            The pouch.
	 * @param tile
	 *            The WorldTile.
	 * @param mapAreaNameHash
	 *            the map area name.
	 * @param canBeAttackFromOutOfArea
	 *            if can be attacked out of area.
	 */
	public Familiar(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(pouch.getNpcId(), tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
		this.owner = owner;
		if (owner.getPerkManager().familiarExpert) {
			hitpoints = getHitpoints();
			setHitpoints((int) (getHitpoints() + (getHitpoints() * 0.25)));
			owner.sendMessage(getName() + "'s hitpoints increased to " + getHitpoints() + " from " + hitpoints
					+ " thanks to Familiar Expert perk.", true);
		}
		this.pouch = pouch;
		resetTicks();
		specialEnergy = 60;
		if (getBOBSize() > 0)
			bob = new BeastOfBurden(canDepositOnly(), getBOBSize());
		call(true);
		setRun(true);
	}

	/**
	 * Checks if the familiar has a withdraw option.
	 * 
	 * @return if has the option.
	 */
	public boolean canDepositOnly() {
		return getDefinitions().hasOption("withdraw");
	}

	/**
	 * Calls the familiar.
	 */
	public void call() {
		call(false);
	}

	/**
	 * Calls the familiar (resets follow & combat).
	 * 
	 * @param login
	 *            if the owner has just logged in.
	 */
	public void call(boolean login) {
		int size = getSize();
		if (login) {
			if (bob != null)
				bob.setEntitys(owner, this);
			checkNearDirs = Utils.getCoordOffsetsNear(size);
			sendMainConfigs();
		} else
			removeTarget();
		WorldTile teleTile = null;
		for (int dir = 0; dir < checkNearDirs[0].length; dir++) {
			final WorldTile tile = new WorldTile(new WorldTile(owner.getX() + checkNearDirs[0][dir],
					owner.getY() + checkNearDirs[1][dir], owner.getPlane()));
			if (World.canMoveNPC(tile.getPlane(), tile.getX(), tile.getY(), size)) {
				teleTile = tile;
				break;
			}
		}
		if (login || teleTile != null)
			setNextGraphics(new Graphics(getDefinitions().size > 1 ? 1315 : 1314));
		if (teleTile == null) {
			if (!sentRequestMoveMessage) {
				owner.sendMessage("There's not enough free space for your familiar to appear.");
				sentRequestMoveMessage = true;
			}
			return;
		}
		sentRequestMoveMessage = false;
		setNextWorldTile(teleTile);
		unlockOrb();
		sendFollowerDetails();
	}

	/**
	 * Checks if the familiar can attack the target.
	 * 
	 * @param target
	 *            The target to check.
	 * @return if can attack.
	 */
	public boolean canAttack(Entity target) {
		if (target == this || target == owner)
			return false;
		if (target instanceof Player) {
			Player player = (Player) target;
			if (!owner.isCanPvp() || !player.isCanPvp())
				return false;
		} else if (target instanceof NPC) {
			NPC n = (NPC) target;
			if (n.getId() == 14301 || n.getId() == 14302 || n.getId() == 14303 || n.getId() == 14304) {
				Glacyte glacyte = (Glacyte) n;
				if (glacyte.getGlacor().getTargetIndex() != -1
						&& getOwner().getIndex() != glacyte.getGlacor().getTargetIndex()) {
					getOwner().sendMessage("This isn't your target.");
					return false;
				}
			}
			if (n.isCantInteract())
				return false;
		}
		return !target.isDead()
				&& ((owner.isAtMultiArea() && isAtMultiArea() && target.isAtMultiArea())
						|| (owner.isForceMultiArea() && target.isForceMultiArea()))
				&& owner.getControlerManager().canAttack(target);
	}

	/**
	 * Checks if the familiar is an essence Beast of Burden.
	 * 
	 * @return if is an essence BoB.
	 */
	public boolean canStoreEssOnly() {
		return pouch.getNpcId() == 6818 || pouch.getPouchId() == Pouches.ABYSSAL_LURKER.getPouchId()
				|| pouch.getPouchId() == Pouches.ABYSSAL_PARASITE.getPouchId()
				|| pouch.getPouchId() == Pouches.ABYSSAL_TITAN.getPouchId();
	}

	/**
	 * Dismisses the familiar.
	 * 
	 * @param logged
	 *            if owner logging out.
	 */
	public void dissmissFamiliar(boolean logged) {
		finish();
		if (!logged && !isFinished()) {
			setFinished(true);
			switchOrb(false);
			owner.getPackets().closeInterface(owner.getInterfaceManager().hasRezizableScreen() ? 98 : 212);
			owner.getPackets().sendIComponentSettings(747, 18, 0, 0, 0);
			if (bob != null)

				bob.dropBob();
			owner.getInterfaceManager().sendSquealOfFortuneTab();
		}
	}

	/**
	 * Drains familiars special attack.
	 */
	public void drainSpecial() {
		specialEnergy -= getSpecialAmount();
		refreshSpecialEnergy();
	}

	/**
	 * Drains familiars special attack.
	 * 
	 * @param specialReduction
	 *            how many points should we reduce.
	 */
	public void drainSpecial(int specialReduction) {
		specialEnergy -= specialReduction;
		if (specialEnergy < 0)
			specialEnergy = 0;
		refreshSpecialEnergy();
	}

	/**
	 * Gets the Beast of Burden familiar.
	 * 
	 * @return BoB.
	 */
	public BeastOfBurden getBob() {
		return bob;
	}

	/**
	 * Gets BoB's inventory size.
	 * 
	 * @return
	 */
	public abstract int getBOBSize();

	/**
	 * Gets familiars original NPC ID.
	 * 
	 * @return
	 */
	public int getOriginalId() {
		return pouch.getNpcId();
	}

	/**
	 * Gets the familiars owner.
	 * 
	 * @return the Player.
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Gets the special amount.
	 * 
	 * @return special amount as Integer.
	 */
	public abstract int getSpecialAmount();

	/**
	 * Gets the special attack type.
	 * 
	 * @return the special attack.
	 */
	public abstract SpecialAttack getSpecialAttack();

	/**
	 * Gets the special attacks description.
	 * 
	 * @return description as String.
	 */
	public abstract String getSpecialDescription();

	/**
	 * Gets the special attacks name.
	 * 
	 * @return name as String.
	 */
	public abstract String getSpecialName();

	/**
	 * Checks if the special attack can be activated.
	 * 
	 * @return if can be activated.
	 */
	public boolean hasSpecialOn() {
		if (owner.getTemporaryAttributtes().remove("FamiliarSpec") != null) {
			if (!owner.getInventory().containsItem(pouch.getScrollId(), 1)) {
				owner.sendMessage("You don't have the scrolls to use this move.", true);
				return false;
			}
			owner.getInventory().deleteItem(pouch.getScrollId(), 1);
			// owner.getInterfaceManager().openGameTab(8);
			drainSpecial();
			return true;
		}
		return false;
	}

	/**
	 * Checks if familiar is aggressive.
	 * 
	 * @return if is aggressive.
	 */
	public boolean isAgressive() {
		return true;
	}

	/**
	 * Checks if familiar is removed.
	 * 
	 * @return if is removed.
	 */
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		unlockOrb();
		trackTimer++;
		if (getName().contains("Bunyip")) {
			if (trackTimer == 15 || trackTimer == 30 || trackTimer == 50 || trackTimer == 75) {
				if (getOwner().getMaxHitpoints() != getOwner().getHitpoints()) {
					getOwner().setNextGraphics(new Graphics(1307));
					int hp = (getOwner().getHitpoints() + (int) (getOwner().getMaxHitpoints() * 0.02));
					getOwner().setHitpoints((hp >= getOwner().getMaxHitpoints() ? getOwner().getMaxHitpoints() : hp));
					getOwner().getSkills().refresh(Skills.HITPOINTS);
				}
			}
		}
		if (trackTimer == (owner.getPerkManager().familiarExpert ? 75 : 50)) {
			trackTimer = 0;
			ticks--;
			if (trackDrain)
				owner.getSkills().drainSummoning(1);
			trackDrain = !trackDrain;
			if (ticks == 2)
				owner.sendMessage("You have 1 minute before your familiar vanishes.");
			else if (ticks == 1)
				owner.sendMessage("You have 30 seconds before your familiar vanishes.");
			else if (ticks == 0) {
				removeFamiliar();
				dissmissFamiliar(false);
				return;
			}
			sendTimeRemaining();
		}
		if (owner.isCanPvp() && getId() != pouch.getNpcId()) {
			transformIntoNPC(pouch.getNpcId());
			call(false);
			return;
		} else if (!owner.isCanPvp() && getId() == pouch.getNpcId()) {
			transformIntoNPC(pouch.getNpcId() - 1);
			call(false);
			return;
		} else if (!withinDistance(owner, 12)) {
			call(false);
			return;
		}
		if (!getCombat().process()) {
			if (isAgressive() && owner.getAttackedBy() != null && owner.getAttackedByDelay() > Utils.currentTimeMillis()
					&& canAttack(owner.getAttackedBy()) && Utils.random(25) == 0)
				getCombat().setTarget(owner.getAttackedBy());
			else
				sendFollow();
		}
	}

	/**
	 * Refreshes the special energy as config.
	 */
	public void refreshSpecialEnergy() {
		owner.getPackets().sendConfig(1177, specialEnergy);
	}

	/**
	 * Removes the familiar.
	 */
	public void removeFamiliar() {
		owner.setFamiliar(null);
	}

	/**
	 * Checks if the familiar can be renewed and handles the action.
	 * 
	 * @return if can be renewed.
	 */
	public boolean renewFamiliar() {
		if (ticks > 5) {
			owner.sendMessage("You can only renew your Familiar starting from 2 minutes 30 seconds.");
			return false;
		} else if (!owner.getInventory().getItems().contains(new Item(pouch.getPouchId(), 1))) {
			owner.sendMessage(
					"You need a " + ItemDefinitions.getItemDefinitions(pouch.getPouchId()).getName().toLowerCase()
							+ " to renew your familiar's timer.",
					true);
			return false;
		}
		resetTicks();
		owner.getInventory().deleteItem(pouch.getPouchId(), 1);
		call(true);
		owner.sendMessage("You use a new pouch to renew your current familiars timer.");
		return true;
	}

	/**
	 * Resets familiars ticks.
	 */
	public void resetTicks() {
		ticks = (int) (pouch.getTime() / 1000 / 30);
		trackTimer = 0;
	}

	/**
	 * Respawns the familiar.
	 * 
	 * @param owner
	 *            The owner as Player.
	 */
	public void respawnFamiliar(Player owner) {
		this.owner = owner;
		initEntity();
		deserialize();
		call(true);
	}

	/**
	 * Handles restoring familiars special attack.
	 * 
	 * @param energy
	 *            by how much should we restore as Integer.
	 */
	public void restoreSpecialAttack(int energy) {
		if (specialEnergy >= 60)
			return;
		specialEnergy = energy + specialEnergy >= 60 ? 60 : specialEnergy + energy;
		refreshSpecialEnergy();
	}

	@Override
	public void sendDeath(Entity source) {
		if (dead)
			return;
		dead = true;
		removeFamiliar();
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		setCantInteract(true);
		getCombat().removeTarget();
		setNextAnimation(null);
		owner.getPackets().sendGlobalConfig(168, 4);

		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
					owner.sendMessage("Your familiar slowly begins to fade away..");
					if (!owner.getPerkManager().familiarExpert)
						owner.sendMessage(
								"Consider purchasing the Familiar Expert perk " + "to increase Pet timers and health.",
								true);
				} else if (loop >= defs.getDeathDelay()) {
					dissmissFamiliar(false);
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	/**
	 * Handles the follow action.
	 */
	private void sendFollow() {
		if (getLastFaceEntity() != owner.getClientIndex())
			setNextFaceEntity(owner);
		if (isFrozen())
			return;
		int size = getSize();
		int targetSize = owner.getSize();
		if (Utils.colides(getX(), getY(), size, owner.getX(), owner.getY(), targetSize) && !owner.hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(owner.getX() + targetSize, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(owner.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), owner.getY() + targetSize)) {
						resetWalkSteps();
						if (!addWalkSteps(getX(), owner.getY() - size)) {
							return;
						}
					}
				}
			}
			return;
		}
		resetWalkSteps();
		if (!clipedProjectile(owner, true)
				|| !Utils.isOnRange(getX(), getY(), size, owner.getX(), owner.getY(), targetSize, 0))
			calcFollow(owner, 2, true, false);
	}

	/**
	 * Sends the time remaining as config.
	 */
	public void sendTimeRemaining() {
		getOwner().getPackets().sendConfig(1176, ticks * 65);
	}

	/**
	 * Sends followers main configs to Owners game client.
	 */
	public void sendMainConfigs() {
		switchOrb(true);
		owner.getPackets().sendConfig(448, pouch.getPouchId());
		owner.getPackets().sendConfig(1160, 243269632); // sets npc emote
		refreshSpecialEnergy();
		sendTimeRemaining();
		owner.getPackets().sendConfig(1175, getSpecialAmount() << 23);
		owner.getPackets().sendGlobalString(204, getSpecialName());
		owner.getPackets().sendGlobalString(205, getSpecialDescription());
		owner.getPackets().sendGlobalConfig(1436, getSpecialAttack() == SpecialAttack.CLICK ? 1 : 0);
		unlockOrb();
	}

	/**
	 * Sends followers main details to Owners game client.
	 */
	public void sendFollowerDetails() {
		boolean res = owner.getInterfaceManager().hasRezizableScreen();
		owner.getPackets().sendInterface(true, res ? 746 : 548, res ? 119 : 179, 662);
		owner.getPackets().sendHideIComponent(662, 44, true);
		owner.getPackets().sendHideIComponent(662, 45, true);
		owner.getPackets().sendHideIComponent(662, 46, true);
		owner.getPackets().sendHideIComponent(662, 47, true);
		owner.getPackets().sendHideIComponent(662, 48, true);
		owner.getPackets().sendHideIComponent(662, 71, false);
		owner.getPackets().sendHideIComponent(662, 72, false);
		owner.getPackets().sendGlobalConfig(168, 8);// tab id
		unlock();
	}

	/**
	 * Handles switching the orb as configs.
	 * 
	 * @param on
	 *            if we're switching the orb on.
	 */
	public void switchOrb(boolean on) {
		owner.getPackets().sendConfig(1174, on ? -1 : 0);
		if (on)
			unlock();
		else
			lockOrb();
	}

	/**
	 * Unlocks the summoning orb.
	 */
	public void unlockOrb() {
		owner.getPackets().sendHideIComponent(747, 9, false);
		sendLeftClickOption(owner);
	}

	/**
	 * Handles selecting of the left-click option on summoning orb.
	 * 
	 * @param player
	 *            The Owner as Player.
	 */
	public static void selectLeftOption(Player player) {
		boolean res = player.getInterfaceManager().hasRezizableScreen();
		player.getPackets().sendInterface(true, res ? 746 : 548, res ? 119 : 179, 880);
		sendLeftClickOption(player);
		player.getPackets().sendGlobalConfig(168, 8);// tab id
	}

	/**
	 * Confirms of the left-click option selection on summoning orb.
	 * 
	 * @param player
	 *            The Owner as Player.
	 */
	public static void confirmLeftOption(Player player) {
		player.getPackets().sendGlobalConfig(168, 4);// inv tab id
		boolean res = player.getInterfaceManager().hasRezizableScreen();
		player.getPackets().closeInterface(res ? 119 : 179);
		player.getInterfaceManager().sendSquealOfFortuneTab();
	}

	/**
	 * Sets the left-click option on summoning orb.
	 * 
	 * @param player
	 *            The Owner as Player.
	 * @param summoningLeftClickOption
	 *            the left-click option as Integer.
	 */
	public static void setLeftclickOption(Player player, int summoningLeftClickOption) {
		if (summoningLeftClickOption == player.getSummoningLeftClickOption())
			return;
		player.setSummoningLeftClickOption(summoningLeftClickOption);
		sendLeftClickOption(player);
	}

	/**
	 * Sends the left-click option to the summoning orb on Owners game client.
	 * 
	 * @param player
	 *            The Owner as Player.
	 */
	public static void sendLeftClickOption(Player player) {
		player.getPackets().sendConfig(1493, player.getSummoningLeftClickOption());
		player.getPackets().sendConfig(1494, player.getSummoningLeftClickOption());
	}

	/**
	 * Unlocks the summoning orb based on the familiar on the Owners game
	 * client.
	 */
	public void unlock() {
		switch (getSpecialAttack()) {
		case CLICK:
			getOwner().getPackets().sendIComponentSettings(747, 18, 0, 0, 2);
			getOwner().getPackets().sendIComponentSettings(662, 74, 0, 0, 2);
			break;
		case ENTITY:
			getOwner().getPackets().sendIComponentSettings(747, 18, 0, 0, 20480);
			getOwner().getPackets().sendIComponentSettings(662, 74, 0, 0, 20480);
			break;
		case OBJECT:
		case ITEM:
			getOwner().getPackets().sendIComponentSettings(747, 18, 0, 0, 65536);
			getOwner().getPackets().sendIComponentSettings(662, 74, 0, 0, 65536);
			break;
		}
		getOwner().getPackets().sendHideIComponent(747, 9, false);
	}

	/**
	 * Locks the orb when there's no familiar active.
	 */
	public void lockOrb() {
		getOwner().getPackets().sendHideIComponent(747, 9, true);
	}

	/**
	 * Turns on or off the familiars special attack.
	 * 
	 * @param on
	 *            if we're turning the special attack on/off
	 */
	public void setSpecial(boolean on) {
		if (!on)
			owner.getTemporaryAttributtes().remove("FamiliarSpec");
		else {
			if (specialEnergy < getSpecialAmount()) {
				owner.sendMessage("You familiar doesn't have enough special energy.");
				return;
			}
			owner.getTemporaryAttributtes().put("FamiliarSpec", Boolean.TRUE);
		}
	}

	/**
	 * Handles BoB's inventory.
	 */
	public void store() {
		if (bob == null)
			return;
		bob.open();
	}

	/**
	 * Handles taking out of BoB's inventory.
	 */
	public void takeBob() {
		if (bob == null)
			return;
		bob.takeBob();
	}

	/**
	 * Switches familiars special attack on the given object.
	 * 
	 * @param object
	 *            The object to execute the special attack on.
	 * @return if can execute special attack.
	 */
	public abstract boolean submitSpecial(Object object);
}