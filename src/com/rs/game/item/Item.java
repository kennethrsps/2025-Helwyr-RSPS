package com.rs.game.item;

import java.io.Serializable;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ItemsEquipIds;

/**
 * Represents a single item.
 */
public class Item implements Serializable {

	private static final long serialVersionUID = -6485003878697568087L;

	private int newId;
	protected int charges;
	
	@Deprecated
	private short id;
	protected int amount;

	public Item(int id) {
		this(id, 1);
	}

	public Item(int id, int amount) {
		this(id, amount, false,0);
	}
	public Item(int id, int amount, int charges) {
		this(id, amount, false, charges);
	}

	public Item(int id, int amount, boolean amt0, int charges) {
		this.newId = id;
		this.amount = amount;
		if (this.amount <= 0 && !amt0) {
			this.amount = 1;
		}
		this.charges = charges;
	}

	
	public Item(Item item) {
		this.newId =  item.getId();
		this.amount = item.getAmount();
		this.charges = item.getCharges();
	}

	@Override
	public Item clone() {
		return new Item(newId, amount);
	}

	public int getAmount() {
		return amount;
	}

	public ItemDefinitions getDefinitions() {
		return ItemDefinitions.getItemDefinitions(newId);
	}
	
	public int getId() {
		return newId != 0 ? newId : id;
	}

	public String getName() {
		return getDefinitions().getName();
	}

	public void setAmount(int amount) {
		if (this.amount + amount < 0 || amount > Integer.MAX_VALUE)
			return;
		this.amount = amount;
	}

	public void setId(int id) {
		this.newId = id;
	}
	
	public int getEquipId() {
		  return ItemsEquipIds.getEquipId(newId);
	}

	public int getCharges() {
		return charges;
	}
	public void setCharges(int charges) {
		if (this.charges + charges < 0 || charges > Integer.MAX_VALUE)
			return;
		this.charges = charges;
	}
}