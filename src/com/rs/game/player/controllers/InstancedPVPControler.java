/*package com.rs.game.player.controllers;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.thieving.Thieving;
import com.rs.game.player.content.InstancedPVP;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;

public class InstancedPVPControler extends Controller {

	private boolean showingSkull;

	@Override
	public void start() {
		sendInterfaces();
	}

	public static void checkBoosts(Player player) {
		boolean changed = false;
		int level = player.getSkills().getLevelForXp(Skills.ATTACK);
		int maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Skills.ATTACK)) {
			player.getSkills().set(Skills.ATTACK, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.STRENGTH);
		maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Skills.STRENGTH)) {
			player.getSkills().set(Skills.STRENGTH, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.DEFENCE);
		maxLevel = (int) (level + 5 + (level * 0.15));
		if (maxLevel < player.getSkills().getLevel(Skills.DEFENCE)) {
			player.getSkills().set(Skills.DEFENCE, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.RANGE);
		maxLevel = (int) (level + 5 + (level * 0.1));
		if (maxLevel < player.getSkills().getLevel(Skills.RANGE)) {
			player.getSkills().set(Skills.RANGE, maxLevel);
			changed = true;
		}
		level = player.getSkills().getLevelForXp(Skills.MAGIC);
		maxLevel = level + 5;
		if (maxLevel < player.getSkills().getLevel(Skills.MAGIC)) {
			player.getSkills().set(Skills.MAGIC, maxLevel);
			changed = true;
		}
		if (changed)
			player.getPackets().sendGameMessage("Your extreme potion bonus has been reduced.");
	}

	@Override
	public boolean login() {
		InstancedPVP.enterInstacedPVP(player, true);
		moved();
		return false;
	}

	@Override
	public boolean keepCombating(Entity target) {
		if (target instanceof NPC)
			return true;
		if (!canAttack(target))
			return false;
		if (target.getAttackedBy() != player && player.getAttackedBy() != target)
			player.setWildernessSkull();
		return true;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			Player p2 = (Player) target;
			if (player.isCanPvp() && !p2.isCanPvp()) {
				player.getPackets().sendGameMessage("That player is not in the wilderness.");
				return false;
			}
			if (canHit(target))
				return true;
			return false;
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		if (target instanceof NPC)
			return true;
		Player p2 = (Player) target;
		if (Math.abs(player.getSkills().getCombatLevel() - p2.getSkills().getCombatLevel()) > getWildLevel())
			return false;
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	public void showSkull() {
		player.getInterfaceManager().sendOverlay(381, false);
	}

	public static boolean isDitch(int id) {
		return id >= 1440 && id <= 1444 || id >= 65076 && id <= 65087;
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (!isDitch(object.getId()) && !object.getDefinitions().name.equalsIgnoreCase("Counter")
				&& !object.getDefinitions().name.equalsIgnoreCase("Bank booth")
				&& !object.getDefinitions().name.equalsIgnoreCase("Bank deposit box")
				&& !object.getDefinitions().name.equalsIgnoreCase("altar")) {
			player.getPackets().sendGameMessage("This object has been disabled.");
			return false;
		}
		if (isDitch(object.getId())) {
			boolean inSide = player.getY() > object.getY();
			player.lock();
			player.setNextAnimation(new Animation(6132));
			final WorldTile toTile = inSide
					? new WorldTile(
							object.getRotation() == 1 || object.getRotation() == 3 ? object.getX() + 2 : player.getX(),
							object.getRotation() == 0 || object.getRotation() == 2 ? object.getY() - 1 : player.getY(),
							object.getPlane())
					: new WorldTile(
							object.getRotation() == 3 || object.getRotation() == 1 ? object.getX() - 1 : player.getX(),
							object.getRotation() == 0 || object.getRotation() == 2 ? object.getY() + 2 : player.getY(),
							object.getPlane());

			player.setNextForceMovement(new ForceMovement(new WorldTile(player), 1, toTile, 2,
					inSide ? (object.getRotation() == 0 || object.getRotation() == 2 ? ForceMovement.SOUTH
							: ForceMovement.EAST)
							: (object.getRotation() == 0 || object.getRotation() == 2 ? ForceMovement.NORTH
									: ForceMovement.WEST)));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(toTile);
					player.faceObject(object);
					if (inSide)
						removeIcon();
					player.resetReceivedDamage();
					player.unlock();
				}
			}, 2);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(final WorldObject object) {
		if (!isDitch(object.getId()) && !object.getDefinitions().name.equalsIgnoreCase("Counter")
				&& !object.getDefinitions().name.equalsIgnoreCase("Bank booth")
				&& !object.getDefinitions().name.equalsIgnoreCase("Bank deposit box")) {
			player.getPackets().sendGameMessage("This object has been disabled.");
			return false;
		}
		if (object.getId() == 2557 || object.getId() == 65717) {
			Thieving.pickDoor(player, object);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick3(WorldObject object) {
		if (!isDitch(object.getId()) && !object.getDefinitions().name.equalsIgnoreCase("Counter")
				&& !object.getDefinitions().name.equalsIgnoreCase("Bank booth")
				&& !object.getDefinitions().name.equalsIgnoreCase("Bank deposit box")) {
			player.getPackets().sendGameMessage("This object has been disabled.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick4(WorldObject object) {
		if (!isDitch(object.getId()) && !object.getDefinitions().name.equalsIgnoreCase("Counter")
				&& !object.getDefinitions().name.equalsIgnoreCase("Bank booth")
				&& !object.getDefinitions().name.equalsIgnoreCase("Bank deposit box")) {
			player.getPackets().sendGameMessage("This object has been disabled.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick5(WorldObject object) {
		if (!isDitch(object.getId()) && !object.getDefinitions().name.equalsIgnoreCase("Counter")
				&& !object.getDefinitions().name.equalsIgnoreCase("Bank booth")
				&& !object.getDefinitions().name.equalsIgnoreCase("Bank deposit box")) {
			player.getPackets().sendGameMessage("This object has been disabled.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == InstancedPVP.MAIN_NPC) {
			npc.faceEntity(player);
			player.getDialogueManager().startDialogue("InstancedPVPD", npc.getId());
			return false;
		}
		return true;
	}

	@Override
	public void sendInterfaces() {
		if (isAtWild(player))
			showSkull();
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		if (interfaceId == 548 || interfaceId == 746) {
			if ((interfaceId == 548 && componentId == 194) || (interfaceId == 746 && componentId == 204)) {
				player.getPackets().sendGameMessage("You can't use money pouch in here.");
				return false;
			} else if ((interfaceId == 746 && componentId == 207) || (interfaceId == 548 && componentId == 159)) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET
						|| packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET
						|| packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					player.getPackets().sendGameMessage("You can't use money pouch in here.");
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean sendDeath() {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					Player killer = player.getMostDamageReceivedSourcePlayer();
					if (killer != null) {
						killer.removeDamage(player);
						killer.increaseKillCount(player);
						sendRewardsOnKill(killer);
					}
					player.sendItemsOnDeath(killer, true);
					player.getEquipment().init();
					player.getInventory().init();
					player.reset();
					player.setNextWorldTile(getTile(3101, 3493, 0));
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					removeIcon();
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean canDropItem(Item item) {
		player.getInventory().deleteItem(item);
		player.getPackets().sendSound(2739, 0, 1);
		return false;
	}

	private void sendRewardsOnKill(Player killer) {
		if (player.getSession().getIP().equalsIgnoreCase(killer.getSession().getIP()))
			return;
		player.setInstancedPVPKillStreak(0);
		int pointsToAdd = 10 + (killer.getInstancedPVPKillStreak());
		killer.setInstancedPVPPoints(killer.getInstancedPVPPoints() + pointsToAdd);
		killer.setInstancedPVPKillStreak(killer.getInstancedPVPKillStreak() + 1);
		killer.getPackets().sendGameMessage("You have recieved " + pointsToAdd + " points.");
	}

	@Override
	public void moved() {
		boolean isAtWild = isAtWild(player);
		boolean isAtWildSafe = isAtWildSafe();
		boolean isInsideInstance = isInsideInstance(player);
		if (!isInsideInstance) {
			InstancedPVP.leaveInstacedPVP(player, false);
			removeControler();
		} else if (!showingSkull && isAtWild && !isAtWildSafe) {
			showingSkull = true;
			player.setCanPvp(true);
			showSkull();
			player.getGlobalPlayerUpdater().generateAppearenceData();
		} else if (showingSkull && (isAtWildSafe || !isAtWild)) {
			removeIcon();
		} else if (!isAtWildSafe && !isAtWild) {
			player.setCanPvp(false);
			removeIcon();
		}
	}

	public void removeIcon() {
		if (showingSkull) {
			showingSkull = false;
			player.setCanPvp(false);
			player.getInterfaceManager().closeOverlay(false);
			player.getGlobalPlayerUpdater().generateAppearenceData();
			player.getEquipment().refresh(null);
		}
	}

	@Override
	public boolean logout() {
		InstancedPVP.leaveInstacedPVP(player, true);
		return false;
	}

	@Override
	public void forceClose() {
		InstancedPVP.leaveInstacedPVP(player, false);
		removeControler();
	}

	public static final boolean isAtWild(WorldTile tile) {
		return (tile.getX() >= getTile(new WorldTile(2944, 3525, 0)).getX()
				&& tile.getX() <= getTile(new WorldTile(3263, 4031, 0)).getX()
				&& tile.getY() >= getTile(new WorldTile(2944, 3525, 0)).getY()
				&& tile.getY() <= getTile(new WorldTile(3263, 4031, 0)).getY());
	}

	public static final boolean isInsideInstance(WorldTile tile) {
		return (tile.getX() >= getTile(new WorldTile(2944, 3456, 0)).getX()
				&& tile.getX() <= getTile(new WorldTile(3263, 4031, 0)).getX()
				&& tile.getY() >= getTile(new WorldTile(2944, 3456, 0)).getY()
				&& tile.getY() <= getTile(new WorldTile(3263, 4031, 0)).getY());
	}

	public boolean isAtWildSafe() {
		return (player.getX() >= getTile(new WorldTile(2944, 3518, 0)).getX()
				&& player.getY() >= getTile(new WorldTile(2944, 3518, 0)).getY()
				&& player.getX() <= getTile(new WorldTile(3263, 3524, 0)).getX()
				&& player.getY() <= getTile(new WorldTile(3263, 3524, 0)).getY());
	}

	public int getWildLevel() {
		int level = ((player.getY() - getTile(new WorldTile(3263, 3520, 0)).getY()) / 8 + 1);
		return level <= 0 ? 0 : level;
	}

	public static int getWildLevel(Player player) {
		int level = ((player.getY() - getTile(new WorldTile(3263, 3520, 0)).getY()) / 8 + 1);
		return level <= 0 ? 0 : level;
	}

	public static boolean isCanSpawnItem(Player player, int itemId) {
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
		String itemName = defs.getName().toLowerCase();
		if (defs.isLended() || itemId == 995 || itemId == 13663 || itemId == 24154 || itemId == 24155 || itemId == 989 || itemName.contains("lamp") || itemName.contains("log")
				|| itemName.contains("keepsake") || itemName.contains("cracker") || itemName.contains("egg")|| itemName.contains("jar") || itemName.contains("ticket")
				|| itemName.contains("token") || itemName.contains("teleport") || itemName.contains("prayer") || itemName.contains("scroll")) 
			return false;
		return true;
	}

	public static WorldTile getTile(WorldTile tile) {
		return InstancedPVP.getTile(tile);
	}

	public static WorldTile getTile(int x, int y, int plane) {
		return InstancedPVP.getTile(x, y, plane);
	}
}
*/