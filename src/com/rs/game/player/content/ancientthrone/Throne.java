package com.rs.game.player.content.ancientthrone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.rs.game.item.Item;
import com.rs.utils.Utils;

public class Throne implements Serializable {
	private static final long serialVersionUID = -2441436436850812203L;

	private String king;
	private int taxRate;
	private List<Item> tax;

	public Throne() {
		setTax(new ArrayList<Item>());
		setTaxRate(0);
		setKing("");
	}

	public String getKingDisplay() {
		return Utils.formatString(getKing());
	}

	public String getKing() {
		return king;
	}

	public void setKing(String king) {
		this.king = king;
	}

	public List<Item> getTax() {
		return tax;
	}

	public void setTax(List<Item> tax) {
		this.tax = tax;
	}

	public int getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(int taxRate) {
		this.taxRate = taxRate;
	}

}
