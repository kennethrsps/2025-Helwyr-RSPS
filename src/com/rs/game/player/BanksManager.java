package com.rs.game.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.rs.game.item.Item;/*
import com.rs.game.player.controllers.InstancedPVPControler;*/

public class BanksManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7369710445123823071L;

	private List<ExtraBank> banks;
	private int activeBankId;

	private transient Player player;

	public BanksManager() {
		banks = new ArrayList<ExtraBank>();
	}

	public List<ExtraBank> getBanks() {
		return banks;
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (banks.isEmpty()) {
			banks.add(new ExtraBank("Bank 1", player.getBank().getBankTabs()));
			setActiveBankId(0, false);
		}
	}

	public int getActiveBankId() {
		return activeBankId;
	}

	public ExtraBank getActiveBank() {
		/*if (player.getControlerManager().getControler() instanceof InstancedPVPControler)
			return player.getPvpBank();*/
		return banks.get(activeBankId);
	}

	public void setActiveBankId(int activeBankId, boolean refresh) {
		if (refresh)
			getActiveBank().setBankTabs(player.getBank().getBankTabs());
		this.activeBankId = activeBankId;
		if (refresh)
			player.getBank().switchBankTabs(banks.get(activeBankId).getBankTabs());
	}

	public void setPVPBank() {
		if (player.getPvpBank() == null)
			player.setPvpBank(new ExtraBank("PVP Bank", new Item[1][0]));
		getActiveBank().setBankTabs(player.getBank().getBankTabs());
		player.getBank().switchBankTabs(player.getPvpBank().getBankTabs());
	}

	public void removePVPBank() {
		player.getPvpBank().setBankTabs(player.getBank().getBankTabs());
		player.getBank().switchBankTabs(banks.get(activeBankId).getBankTabs());
	}

	public static final class ExtraBank implements Serializable {

		private static final long serialVersionUID = -208156332220130683L;
		private String name;
		private Item[][] bankTabs;

		public ExtraBank(String name, Item[][] bankTabs) {
			this.name = name;
			this.bankTabs = bankTabs;
		}

		public String getName() {
			return name;
		}

		public Item[][] getBankTabs() {
			return bankTabs;
		}

		public void setBankTabs(Item[][] bankTabs) {
			this.bankTabs = bankTabs;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
