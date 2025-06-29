/*package com.rs.game.player.content;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class ReferralHandler {

	private static HashMap<String, String> REFERRAL_MAP;

	public static void init() {
		REFERRAL_MAP = SerializableFilesManager.loadReferralMap();
	}

	public static void addReferral(String player, String other) {
		if (REFERRAL_MAP == null)
			REFERRAL_MAP = SerializableFilesManager.loadReferralMap();
		REFERRAL_MAP.put(player, other);
	}

	public static void processReferralRewards() {
		if (REFERRAL_MAP == null)
			REFERRAL_MAP = SerializableFilesManager.loadReferralMap();
		if (REFERRAL_MAP.isEmpty())
			return;
		for (Map.Entry<String, String> entery : REFERRAL_MAP.entrySet()) {
			if (entery.getValue() == null)
				continue;
			boolean pOnline = true;
			boolean oOnline = true;
			Player player = World.getPlayerByDisplayName(entery.getKey());
			Player other = World.getPlayerByDisplayName(entery.getValue());
			if (player == null) {
				player = SerializableFilesManager.loadPlayer(entery.getKey());
				pOnline = false;
			}
			if (other == null) {
				other = SerializableFilesManager.loadPlayer(entery.getValue());
				oOnline = false;
			}
			if (other == null || player == null)
				continue;
			if (((other.getCurrentMac() != null && player.getCurrentMac() != null)
					&& other.getCurrentMac().equalsIgnoreCase(player.getCurrentMac()))
					|| other.getLastIP().equalsIgnoreCase(player.getLastIP()) || other.isRecievedReferralReward()
					|| player.getTimePlayed(pOnline) < (10 * 60 * 60 * 1000))
				continue;

			other.setRecievedReferralReward(true);
			other.getBank().addItem(995, 10000000, oOnline);
			other.getBank().addItem(24154, 1, oOnline);
			if (oOnline)
				other.getPackets()
						.sendGameMessage("<col=00ff00>You have recieved 10m cash and 1 spin ticket for referraling "
								+ entery.getKey() + ". Your reward has sent to your bank.");
			else
				SerializableFilesManager.savePlayer(other);
		}
	}

	public static void processReferralDonation(Player player, int amount) {
		String referralled = REFERRAL_MAP.containsKey(player.getDisplayName())
				? REFERRAL_MAP.get(player.getDisplayName()) : null;
		if (referralled == null || amount < 10)
			return;
		boolean oOnline = true;
		Player other = World.getPlayerByDisplayName(referralled);
		if (other == null) {
			other = SerializableFilesManager.loadPlayer(referralled);
			oOnline = false;
		}
		other.setReferralPoints((int) (other.getReferralPoints() + (amount * 0.1)));
		if (oOnline)
			other.getPackets().sendGameMessage("<col=00ff00>You have recieved " + (amount * 0.1)
					+ " referral points. You can spend them by ::referralrewards.");
		else
			SerializableFilesManager.savePlayer(other);
	}

	public static final void save() {
		SerializableFilesManager.saveReferralMap(REFERRAL_MAP);
	}

}*/