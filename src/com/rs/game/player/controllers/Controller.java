package com.rs.game.player.controllers;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.Foods.Food;
import com.rs.game.player.content.Pots.Pot;

public abstract class Controller {

	// private static final long serialVersionUID = 8384350746724116339L;

	protected Player player;

	public boolean canAddInventoryItem(int itemId, int amount) {
		return true;
	}

	/**
	 * after the normal checks, extra checks, only called when you start trying
	 * to attack
	 */
	public boolean canAttack(Entity target) {
		return true;
	}

	public boolean canDeleteInventoryItem(int itemId, int amount) {
		return true;
	}

	public boolean canDropItem(Item item) {
		return true;
	}

	public boolean canEat(Food food) {
		return true;
	}

	public boolean canEquip(int slotId, int itemId) {
		return true;
	}

	/**
	 * hits as ice barrage and that on multi areas
	 */
	public boolean canHit(Entity entity) {
		return true;
	}

	/**
	 * return can move that step
	 */
	public boolean canMove(int dir) {
		return true;
	}

	public boolean canPlayerOption1(Player target) {
		return true;
	}

	public boolean canPot(Pot pot) {
		return true;
	}

	public boolean canSummonFamiliar() {
		return true;
	}

	/**
	 * check if you can use commands in the controller
	 */
	public boolean processCommand(String s, boolean b, boolean c) {
		return true;
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		return true;
	}

	public boolean canWalk() {
		return true;
	}

	/**
	 * return can set that step
	 */
	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		return true;
	}

	public void forceClose() {
	}

	public final Object[] getArguments() {
		return player.getControlerManager().getLastControlerArguments();
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * after the normal checks, extra checks, only called when you attacking
	 */
	public boolean keepCombating(Entity target) {
		return true;
	}

	/**
	 * return remove controler
	 */
	public boolean login() {
		return true;
	}

	/**
	 * return remove controler
	 */
	public boolean logout() {
		return true;
	}

	/**
	 * called once teleport is performed
	 */
	public void magicTeleported(int type) {

	}

	public void moved() {

	}

	/**
	 * processes every game tick, usualy not used
	 */
	public void process() {

	}

	/**
	 * return process normaly
	 */
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
		return true;
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		return true;
	}

	public boolean processItemOnPlayer(Player player, int itemId) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processItemTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processMagicTeleport(WorldTile toTile) {
		return true;
	}

	public boolean processMoneyPouch() {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick1(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick2(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick3(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick4(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick1(WorldObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick2(WorldObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick3(WorldObject object) {
		return true;
	}

	public boolean processObjectClick5(WorldObject object) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processObjectTeleport(WorldTile toTile) {
		return true;
	}

	public final void removeControler() {
		player.getControlerManager().removeControlerWithoutCheck();
	}

	/**
	 * return let default death
	 */
	public boolean sendDeath() {
		return true;
	}

	public void sendInterfaces() {

	}

	public final void setArguments(Object[] objects) {
		player.getControlerManager().setLastControlerArguments(objects);
	}

	public final void setPlayer(Player player) {
		this.player = player;
	}

	public abstract void start();

	public void trackXP(int skillId, int addedXp) {

	}

	/**
	 * return can use script
	 */
	public boolean useDialogueScript(Object key) {
		return true;
	}

	public boolean handleItemOnObject(WorldObject object, Item item) {
		return true;
	}

	public boolean processObjectClick4(WorldObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean canTakeItem(FloorItem item) {
		return true;
	}

	/**
	 * return process normaly
	 * 
	 * @param slotId2
	 *            TODO
	 */
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		return true;
	}

	public boolean canRemoveEquip(int slotId, int itemId) {
		return true;
	}

	public void processNPCDeath(int id) {

	}

	public void processIngoingHit(final Hit hit) {

	}

	public void processIncommingHit(final Hit hit, Entity target) {

	}
	
	public boolean processItemOnPlayer(Player target, Item item, int slot) {
		return true;
	}
	
	public boolean canUseItemOnPlayer(Player p2, Item item) {
		return true;
	}
	
	public void processNPCDeath(NPC npc) {

	}
}