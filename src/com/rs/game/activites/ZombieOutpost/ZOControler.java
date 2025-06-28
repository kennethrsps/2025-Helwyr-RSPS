package com.rs.game.activites.ZombieOutpost;

import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.Foods.Food;
import com.rs.game.player.content.Pots.Pot;
import com.rs.game.player.controllers.Controller;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class ZOControler extends Controller {

	@Override
	public boolean canAddInventoryItem(int itemId, int amount) {
		return true;
	}
	
	@Override
	public boolean canAttack(Entity target) {
		return true;
	}

	@Override
	public boolean canDeleteInventoryItem(int itemId, int amount) {
		return true;
	}

	@Override
	public boolean canDropItem(Item item) {
		return true;
	}

	@Override
	public boolean canEat(Food food) {
		return true;
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		return true;
	}

	/**
	 * hits as ice barrage and that on multi areas
	 */
	@Override
	public boolean canHit(Entity entity) {
		return true;
	}

	/**
	 * return can move that step
	 */
	@Override
	public boolean canMove(int dir) {
		return true;
	}

	@Override
	public boolean canPlayerOption1(Player target) {
		return true;
	}

	@Override
	public boolean canPot(Pot pot) {
		return true;
	}

	@Override
	public boolean canSummonFamiliar() {
		return true;
	}

	/**
	 * check if you can use commands in the controller
	 */
	@Override
	public boolean processCommand(String s, boolean b, boolean c) {
		return true;
	}

	@Override
	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		return true;
	}

	@Override
	public boolean canWalk() {
		return true;
	}

	/**
	 * return can set that step
	 */
	@Override
	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		return true;
	}

	@Override
	public void forceClose() {
		
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	/**
	 * after the normal checks, extra checks, only called when you attacking
	 */
	@Override
	public boolean keepCombating(Entity target) {
		return true;
	}

	/**
	 * return remove controler
	 */
	@Override
	public boolean login() {
		return true;
	}

	/**
	 * return remove controler
	 */
	@Override
	public boolean logout() {
		return true;
	}

	/**
	 * called once teleport is performed
	 */
	@Override
	public void magicTeleported(int type) {

	}

	@Override
	public void moved() {

	}

	/**
	 * processes every game tick, usualy not used
	 */
	@Override
	public void process() {

	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
		return true;
	}

	@Override
	public boolean processItemOnNPC(NPC npc, Item item) {
		return true;
	}

	@Override
	public boolean processItemOnPlayer(Player player, int itemId) {
		return true;
	}

	/**
	 * return can teleport
	 */
	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		return false;
	}

	/**
	 * return can teleport
	 */
	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public boolean processMoneyPouch() {
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processNPCClick1(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processNPCClick2(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processNPCClick3(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processNPCClick4(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick1(WorldObject object) {
		if(object.getId() == 82224) {
			ZombieShop.openZombieShop1(player);
			return true;
		}
		TowerObject tower = TowerObject.getTowerForObject(object);
		if(tower != null) {
			TowerObject.sendInterface(player, tower);
			return true;
		}
		return false;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick2(WorldObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick3(WorldObject object) {
		return true;
	}

	@Override
	public boolean processObjectClick5(WorldObject object) {
		return true;
	}

	/**
	 * return can teleport
	 */
	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		return false;
	}

	/**
	 * return let default death
	 */
	@Override
	public boolean sendDeath() {
		return true;
	}

	@Override
	public void sendInterfaces() {

	}

	@Override
	public void trackXP(int skillId, int addedXp) {

	}

	/**
	 * return can use script
	 */
	@Override
	public boolean useDialogueScript(Object key) {
		return true;
	}

	@Override
	public boolean handleItemOnObject(WorldObject object, Item item) {
		if(object.getId() == WallObject.BROKEN_WALL && item.getId() == 2347) {
			WallObject wall = WallObject.getWallForObject(object);
			if(wall != null) {
				wall.repair(player, object);
			}
			return true;
		}
		TowerObject tower = TowerObject.getTowerForObject(object);
		if(tower != null) {
			TowerObject.sendInterface(player, tower);
			return true;
		}
		return false;
	}

	@Override
	public boolean processObjectClick4(WorldObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean canTakeItem(FloorItem item) {
		return true;
	}

	/**
	 * return process normaly
	 * 
	 * @param slotId2
	 *            TODO
	 */
	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		return true;
	}

	@Override
	public boolean canRemoveEquip(int slotId, int itemId) {
		return true;
	}

	@Override
	public void processNPCDeath(int id) {

	}

	@Override
	public void processIngoingHit(final Hit hit) {

	}

	@Override
	public void processIncommingHit(final Hit hit, Entity target) {

	}

	@Override
	public boolean processItemOnPlayer(Player target, Item item, int slot) {
		return true;
	}

	@Override
	public boolean canUseItemOnPlayer(Player p2, Item item) {
		return true;
	}

	@Override
	public void processNPCDeath(NPC npc) {

	}

	@Override
	public void start() {
		
		
	}

}
