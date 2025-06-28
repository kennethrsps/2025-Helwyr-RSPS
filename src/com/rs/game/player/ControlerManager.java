package com.rs.game.player;

import java.io.Serializable;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.content.Foods.Food;
import com.rs.game.player.content.Pots.Pot;
import com.rs.game.player.controllers.Controller;
import com.rs.game.player.controllers.ControllerHandler;

public final class ControlerManager implements Serializable {

	private static final long serialVersionUID = 2084691334731830796L;

	private transient Player player;
	private transient Controller controler;
	private transient boolean inited;
	private Object[] lastControlerArguments;

	private String lastControler;

	public ControlerManager() {

	}

	public boolean canAddInventoryItem(int itemId, int amount) {
		if (controler == null || !inited)
			return true;
		return controler.canAddInventoryItem(itemId, amount);
	}

	public boolean canAttack(Entity entity) {
		if (controler == null || !inited)
			return true;
		return controler.canAttack(entity);
	}

	public boolean canDeleteInventoryItem(int itemId, int amount) {
		if (controler == null || !inited)
			return true;
		return controler.canDeleteInventoryItem(itemId, amount);
	}

	public boolean canDropItem(Item item) {
		if (controler == null || !inited)
			return true;
		return controler.canDropItem(item);
	}

	public boolean canEat(Food food) {
		if (controler == null || !inited)
			return true;
		return controler.canEat(food);
	}

	public boolean canEquip(int slotId, int itemId) {
		if (controler == null || !inited)
			return true;
		return controler.canEquip(slotId, itemId);
	}

	public boolean canHit(Entity entity) {
		if (controler == null || !inited)
			return true;
		return controler.canHit(entity);
	}

	public boolean canMove(int dir) {
		if (controler == null || !inited)
			return true;
		return controler.canMove(dir);
	}

	public boolean canPlayerOption1(Player target) {
		if (controler == null || !inited)
			return true;
		return controler.canPlayerOption1(target);
	}

	public boolean canPot(Pot pot) {
		if (controler == null || !inited)
			return true;
		return controler.canPot(pot);
	}

	public boolean canSummonFamiliar() {
		if (controler == null || !inited)
			return true;
		return controler.canSummonFamiliar();
	}

	public boolean processCommand(String s, boolean b, boolean c) {
		if (controler == null || !inited)
			return true;
		return controler.processCommand(s, b, c);
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		if (controler == null || !inited)
			return true;
		return controler.canUseItemOnItem(itemUsed, usedWith);
	}

	public boolean canWalk() {
		if (controler == null || !inited)
			return true;
		return controler.canWalk();
	}

	public void forceStop() {
		if (controler != null) {
			controler.forceClose();
			controler = null;
		}
		lastControlerArguments = null;
		lastControler = null;
		inited = false;
	}

	public Controller getControler() {
		return controler;
	}

	public Object[] getLastControlerArguments() {
		return lastControlerArguments;
	}

	public boolean keepCombating(Entity target) {
		if (controler == null || !inited)
			return true;
		return controler.keepCombating(target);
	}

	public void login() {
		if (lastControler == null)
			return;
		controler = ControllerHandler.getControler(lastControler);
		if (controler == null) {
			forceStop();
			return;
		}
		controler.setPlayer(player);
		if (controler.login())
			forceStop();
		else
			inited = true;
	}

	public void logout() {
		if (controler == null)
			return;
		if (controler.logout())
			forceStop();
	}

	public void magicTeleported(int type) {
		if (controler == null || !inited)
			return;
		controler.magicTeleported(type);
	}

	public void moved() {
		if (controler == null || !inited)
			return;
		controler.moved();
	}

	public void process() {
		if (controler == null || !inited)
			return;
		controler.process();
	}

	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
		if (controler == null || !inited)
			return true;
		return controler.processButtonClick(interfaceId, componentId, slotId, packetId);
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		if (controler == null || !inited)
			return true;
		return controler.processItemOnNPC(npc, item);
	}

	public boolean processItemOnPlayer(Player player, int itemId) {
		if (controler == null || !inited)
			return true;
		return controler.processItemOnPlayer(player, itemId);
	}

	public boolean processItemTeleport(WorldTile toTile) {
		if (controler == null || !inited)
			return true;
		return controler.processItemTeleport(toTile);
	}

	public boolean processMagicTeleport(WorldTile toTile) {
		if (controler == null || !inited)
			return true;
		return controler.processMagicTeleport(toTile);
	}

	public boolean processMoneyPouch() {
		if (controler == null || !inited)
			return true;
		return controler.processMoneyPouch();
	}

	public boolean processNPCClick1(NPC npc) {
		if (controler == null || !inited)
			return true;
		return controler.processNPCClick1(npc);
	}

	public boolean processNPCClick2(NPC npc) {
		if (controler == null || !inited)
			return true;
		return controler.processNPCClick2(npc);
	}

	public boolean processNPCClick3(NPC npc) {
		if (controler == null || !inited)
			return true;
		return controler.processNPCClick3(npc);
	}

	public boolean processNPCClick4(NPC npc) {
		if (controler == null || !inited)
			return true;
		return controler.processNPCClick4(npc);
	}

	public boolean processObjectClick1(WorldObject object) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectClick1(object);
	}

	public boolean processObjectClick2(WorldObject object) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectClick2(object);
	}

	public boolean processObjectClick3(WorldObject object) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectClick3(object);
	}

	public boolean processObjectClick4(WorldObject object) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectClick4(object);
	}

	public boolean processObjectClick5(WorldObject object) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectClick5(object);
	}

	public boolean processObjectTeleport(WorldTile toTile) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectTeleport(toTile);
	}

	public void removeControlerWithoutCheck() {
		controler = null;
		lastControlerArguments = null;
		lastControler = null;
		inited = false;
	}

	public boolean sendDeath() {
		if (controler == null || !inited)
			return true;
		return controler.sendDeath();
	}

	public void sendInterfaces() {
		if (controler == null || !inited)
			return;
		controler.sendInterfaces();
	}

	public void setLastControlerArguments(Object[] lastControlerArguments) {
		this.lastControlerArguments = lastControlerArguments;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void startControler(Object key, Object... parameters) {
		if (controler != null)
			forceStop();
		controler = (Controller) (key instanceof Controller ? key : ControllerHandler.getControler(key));
		if (controler == null)
			return;
		controler.setPlayer(player);
		lastControlerArguments = parameters;
		lastControler = (String) key;
		controler.start();
		inited = true;
	}

	public void trackXP(int skillId, int addedXp) {
		if (controler == null || !inited)
			return;
		controler.trackXP(skillId, addedXp);
	}

	public boolean useDialogueScript(Object key) {
		if (controler == null || !inited)
			return true;
		return controler.useDialogueScript(key);
	}

	public boolean addWalkStep(int lastX, int lastY, int nextX, int nextY) {
		if (controler == null || !inited)
			return true;
		return controler.checkWalkStep(lastX, lastY, nextX, nextY);
	}

	public boolean handleItemOnObject(WorldObject object, Item item) {
		if (controler == null || !inited)
			return true;
		return controler.handleItemOnObject(object, item);
	}

	public boolean canRemoveEquip(int slotId, int itemId) {
		if (controler == null || !inited)
			return true;
		return controler.canRemoveEquip(slotId, itemId);
	}

	public void processNPCDeath(int id) {
		if (controler == null || !inited)
			return;
		controler.processNPCDeath(id);
	}

	public void processIncommingHit(Hit hit, Entity target) {
		if (controler == null || !inited)
			return;
		controler.processIncommingHit(hit, target);
	}

	public void processIngoingHit(Hit hit) {
		if (controler == null || !inited)
			return;
		controler.processIngoingHit(hit);
	}

	public boolean processItemOnPlayer(Player p2, Item item, int slot) {
		if (controler == null || !inited)
			return true;
		return controler.processItemOnPlayer(p2, item, slot);
	}

	public boolean canUseItemOnPlayer(Player p2, Item item) {
		if (controler == null || !inited)
			return true;
		return controler.canUseItemOnPlayer(p2, item);
	}

	public void processNPCDeath(NPC npc) {
		if (controler == null || !inited)
			return;
		controler.processNPCDeath(npc);
	}

	public boolean canTakeItem(FloorItem item) {
		if (controler == null || !inited)
			return true;
		return controler.canTakeItem(item);
	}
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		if (controler == null || !inited)
			return true;
		return controler.processButtonClick(interfaceId, componentId, slotId, slotId2, packetId);
	}

	
}