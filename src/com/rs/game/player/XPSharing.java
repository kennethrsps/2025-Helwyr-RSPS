package com.rs.game.player;

import java.io.Serializable;

import com.rs.game.World;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Utils;

public class XPSharing implements Serializable {
	private static final long serialVersionUID = -6325610155180543271L;
	
	private String receiverName;
	private Player player;
	private long entryTimer;
	
	public static final int ITEM_ID = 0;
	
	public void sendEntry() {
		player.getTemporaryAttributtes().put("xp_share", Boolean.TRUE);
		player.getPackets().sendRunScript(109, new Object[] {"Who do you want to XP share with?"});
	}
	
	public void submitEntry(String name) {
		name = Utils.formatPlayerNameForProtocol(name);
		if(getEntryTimer() > Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("You still need to wait "+Utils.getFormattedTime((getEntryTimer()-Utils.currentTimeMillis()), false)+" to change your XP share.");
			return;
		}
		if(name.equals(player.getUsername())) {
			player.getPackets().sendGameMessage("You can't XP share with yourself.");
			return;
		}
		if(player.getSkills().getTotalLevel() < 100) {
			player.getPackets().sendGameMessage("You need over 100 total level to XP share with people.");
			return;
		}
		if(!player.getInventory().containsItem(ITEM_ID, 1)) {
			return;
		}
		Player entry = World.getPlayer(name);
		String displayName = Utils.formatString(name);
		if(entry == null) {
			player.getPackets().sendGameMessage(displayName+" isn't online currently.");
			return;
		}
		if(entry.getSkills().getTotalLevel() < 100) {
			player.getPackets().sendGameMessage(displayName+" needs over 100 total level to XP Share with them.");
			return;
		}
		if(getAccountTypeId() != entry.getXpSharing().getAccountTypeId()) {
			player.getPackets().sendGameMessage("You are not the same account XP type as "+displayName+".");
			return;
		}
		entry.getXpSharing().sendConfirmation(player);
	}
	
	public void sendXPShare(int skillId, double exp) {
		if(getReceiverName() == null) {
			return;
		}
		if(getReceiverName().equals("")) {
			return;
		}
		Player receiver = World.getPlayer(getReceiverName());
		if(receiver == null) {
			return;
		}
		double sharedXP = (exp / 100) * 25;
		if(sharedXP > 0) {
			receiver.getSkills().addXp(skillId, sharedXP, true, false);
		}
	}
	
	public void sendConfirmation(Player sharer) {
		player.getDialogueManager().startDialogue(new Dialogue() {

			@Override
			public void start() {
				sendOptionsDialogue(sharer.getDisplayName()+" wants to share 25% of<br>their XP with you. Accept?", "Yes", "No");
			}

			@Override
			public void run(int interfaceId, int componentId) {
				if(componentId == 11) {
					sharer.getXpSharing().setReceiverName(player.getUsername());
					player.getPackets().sendGameMessage(sharer.getDisplayName()+" is now sharing their XP with you.");
					sharer.getPackets().sendGameMessage("You're now sharing XP with "+player.getDisplayName());
					sharer.getXpSharing().setEntryTimer(Utils.currentTimeMillis() + 3600000);//1 HOUR
					sharer.getInventory().deleteItem(ITEM_ID, 1);
					end();
				} else {
					end();
				}
			}

			@Override
			public void finish() {
			}
		
		});
	}
	
	public int getAccountTypeId() {
		return player.veteran ? 0 : player.intermediate ? 1 : player.easy ? 2 : player.ironman ? 3 : player.hcironman ? 4 : player.expert ? 5 : 6;
	}
	
	public XPSharing(Player player) {
		this.player = player;
	}
	
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}

	public long getEntryTimer() {
		return entryTimer;
	}

	public void setEntryTimer(long entryTimer) {
		this.entryTimer = entryTimer;
	}

}
