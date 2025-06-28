package com.rs.game.player.actions;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.Player;

public class DummyEntry {
	
	public static List<DummyEntry> entries = new ArrayList<DummyEntry>();
	public static int DAMAGE_CAP = 100;
	
	public static void addDamage(String player, int damage) {
		for(DummyEntry entry : entries) {
			if(entry.getPlayer() == player) {
				entry.addDamage(damage);
				return;
			}
		}
		DummyEntry entry = new DummyEntry(player);
		entries.add(entry);
		addDamage(player, damage);
	}
	
	public static boolean reachedDamageCap(String player) {
		for(DummyEntry entry : entries) {
			if(entry.getPlayer().equals(player)) {
				if(entry.getDamage() > DAMAGE_CAP) {
					return true;
				}
			}
		}
		return false;
	}

	public String name;
	public int damage;
	
	public DummyEntry(String player) {
		setPlayer(player);
		setDamage(0);
	}

	public String getPlayer() {
		return name;
	}

	public void setPlayer(String player) {
		this.name = player;
	}
	
	public void addDamage(int dmg) {
		damage += dmg;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}
	
}
